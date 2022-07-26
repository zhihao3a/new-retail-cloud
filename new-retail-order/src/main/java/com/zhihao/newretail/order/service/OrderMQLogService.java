package com.zhihao.newretail.order.service;

import com.zhihao.newretail.order.pojo.OrderMQLog;

import java.util.List;

/*
 * @Project: NewRetail-Cloud
 * @Author: Zhihao
 * @Email: cafebabe0508@163.com
 * */
public interface OrderMQLogService {

    /*
     * 保存消息
     * */
    int insetMessage(Long messageId, String content, String exchange, String routingKey);

    /*
     * 更新消息
     * */
    int updateMessage(OrderMQLog orderMqLog);

    /*
     * 删除消息
     * */
    void deleteMessage(Long messageId);

    /*
     * 获取消息信息
     * */
    OrderMQLog getMQLog(Long messageId);

    /*
    * 获取发送状态异常消息
    * */
    List<OrderMQLog> listOrderMQLogS(Integer status);

    /*
    * 获取消息唯一id
    * */
    Long getMessageId();

}
