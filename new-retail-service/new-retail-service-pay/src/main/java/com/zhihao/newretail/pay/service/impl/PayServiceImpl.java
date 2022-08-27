package com.zhihao.newretail.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zhihao.newretail.api.order.feign.OrderFeignService;
import com.zhihao.newretail.api.order.vo.OrderPayInfoApiVO;
import com.zhihao.newretail.core.enums.DeleteEnum;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.core.util.GsonUtil;
import com.zhihao.newretail.pay.config.AliPayPCPramConfig;
import com.zhihao.newretail.pay.enums.OrderStatusEnum;
import com.zhihao.newretail.pay.enums.PayPlatformEnum;
import com.zhihao.newretail.pay.pojo.PayInfo;
import com.zhihao.newretail.pay.service.PayInfoMQLogService;
import com.zhihao.newretail.pay.service.PayInfoService;
import com.zhihao.newretail.pay.service.PayService;
import com.zhihao.newretail.rabbitmq.consts.RabbitMQConst;
import com.zhihao.newretail.rabbitmq.dto.pay.PayNotifyMQDTO;
import com.zhihao.newretail.rabbitmq.util.MyRabbitMQUtil;
import org.apache.http.HttpStatus;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/*
 * @Project: NewRetail-Cloud
 * @Author: Zhihao
 * @Email: cafebabe0508@163.com
 * */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private AliPayPCPramConfig aliPayPCPramConfig;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private PayInfoService payInfoService;

    @Autowired
    private PayInfoMQLogService payInfoMqLogService;

    @Autowired
    private MyRabbitMQUtil rabbitMQUtil;

    private static final String PAY_SUCCESS = "TRADE_SUCCESS";

    @Override
    public String getBody(Long orderId) throws AlipayApiException {
        OrderPayInfoApiVO orderPayInfoApiVO = orderFeignService.getOrderPayInfoApiVO(orderId);
        if (ObjectUtils.isEmpty(orderPayInfoApiVO.getId()) || DeleteEnum.DELETE.getCode().equals(orderPayInfoApiVO.getIsDelete())) {
            throw new ServiceException(HttpStatus.SC_NOT_FOUND, "订单不存在");
        }
        if (!OrderStatusEnum.NOT_PAY.getCode().equals(orderPayInfoApiVO.getStatus())) {
            throw new ServiceException("订单已关闭支付");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayPCPramConfig.getGateway(),
                aliPayPCPramConfig.getAppId(),
                aliPayPCPramConfig.getPrivateKey(),
                "json",
                "utf-8",
                aliPayPCPramConfig.getAlipayPublicKey(),
                "RSA2"
        );
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(aliPayPCPramConfig.getReturnUrl());
        request.setNotifyUrl(aliPayPCPramConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderPayInfoApiVO.getId());
        bizContent.put("total_amount", orderPayInfoApiVO.getActualAmount());
        bizContent.put("subject", "商品消费");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);

        if (response.isSuccess()) {
            PayInfo payInfo = payInfoService.getPayInfo(orderId);
            if (ObjectUtils.isEmpty(payInfo)) {
                payInfo = buildPayInfo(orderPayInfoApiVO);
                payInfoService.insertPayInfo(payInfo);
            }
            return response.getBody();
        } else {
            throw new ServiceException("支付模块异常");
        }
    }

    @Override
    public void asyncNotify(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException, AlipayApiException {
        req.setCharacterEncoding("utf-8");
        String tradeNo = req.getParameter("trade_no");          // 支付宝流水号
        String outTradeNo = req.getParameter("out_trade_no");   // 订单号
        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayPCPramConfig.getGateway(),
                aliPayPCPramConfig.getAppId(),
                aliPayPCPramConfig.getPrivateKey(),
                "json",
                "utf-8",
                aliPayPCPramConfig.getAlipayPublicKey(),
                "RSA2"
        );
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        String tradeStatus = response.getTradeStatus();

        if (PAY_SUCCESS.equals(tradeStatus)) {
            /* 支付成功，更新数据库支付信息 */
            PayInfo payInfo = payInfoService.getPayInfo(Long.valueOf(outTradeNo));
            payInfo.setStatus(OrderStatusEnum.PAY_SUCCEED.getCode());
            payInfo.setPlatformNumber(tradeNo);
            payInfoService.updatePayInfo(payInfo);

            /* 发送消息，更新订单状态 */
            PayNotifyMQDTO payNotifyMQDTO = buildPayNotifyMQDTO(payInfo);
            String content = GsonUtil.obj2Json(payNotifyMQDTO);
            Long messageId = payInfoMqLogService.getMessageId();
            String exchange = RabbitMQConst.PAY_NOTIFY_EXCHANGE;
            String routingKey = RabbitMQConst.PAY_SUCCESS_ROUTING_KEY;
            payInfoMqLogService.insetMessage(messageId, content, exchange, routingKey);
            sendPaySuccessMessage(exchange, routingKey, content, messageId);
        }
    }

    private PayInfo buildPayInfo(OrderPayInfoApiVO orderPayInfoApiVO) {
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderId(orderPayInfoApiVO.getId());
        payInfo.setUserId(orderPayInfoApiVO.getUserId());
        payInfo.setPayAmount(orderPayInfoApiVO.getActualAmount());
        payInfo.setPayPlatform(PayPlatformEnum.ALIPAY_PC.getCode());
        payInfo.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        return payInfo;
    }

    private PayNotifyMQDTO buildPayNotifyMQDTO(PayInfo payInfo) {
        PayNotifyMQDTO payNotifyMQDTO = new PayNotifyMQDTO();
        payNotifyMQDTO.setOrderNo(payInfo.getOrderId());
        payNotifyMQDTO.setUserId(payInfo.getUserId());
        payNotifyMQDTO.setPlatformNumber(payInfo.getPlatformNumber());
        payNotifyMQDTO.setPayAmount(payInfo.getPayAmount());
        payNotifyMQDTO.setPayPlatform(payInfo.getPayPlatform());
        payNotifyMQDTO.setStatus(payInfo.getStatus());
        payNotifyMQDTO.setMqVersion(RabbitMQConst.CONSUME_VERSION);
        return payNotifyMQDTO;
    }

    private void sendPaySuccessMessage(String exchange, String routingKey, String content, Long messageId) {
        rabbitMQUtil.sendMessage(exchange, routingKey, content, new CorrelationData(String.valueOf(messageId)));
    }

}
