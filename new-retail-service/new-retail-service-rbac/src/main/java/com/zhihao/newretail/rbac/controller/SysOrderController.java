package com.zhihao.newretail.rbac.controller;

import com.zhihao.newretail.api.order.vo.OrderApiVO;
import com.zhihao.newretail.core.exception.ServiceException;
import com.zhihao.newretail.core.util.PageUtil;
import com.zhihao.newretail.core.util.R;
import com.zhihao.newretail.rbac.context.SysUserTokenContext;
import com.zhihao.newretail.rbac.service.SysOrderService;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.security.context.UserLoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class SysOrderController {

    @Autowired
    private SysOrderService sysOrderService;

    @RequiresLogin
    @GetMapping("/order/list")
    public R orderList(@RequestParam(required = false) Long orderNo,
                       @RequestParam(required = false) Integer userId,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (pageNum == 0 || pageSize == 0) {
            throw new ServiceException("分页参数不能为0");
        }
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        PageUtil<OrderApiVO> pageData = sysOrderService.listOrderApiVOSByPage(orderNo, userId, status, pageNum, pageSize);
        UserLoginContext.sysClean();
        return R.ok().put("data", pageData);
    }

    @RequiresLogin
    @PutMapping("/order/{orderNo}")
    public R deliverGoods(@PathVariable Long orderNo) {
        String userToken = UserLoginContext.getSysUserLoginVO().getUserToken();
        SysUserTokenContext.setUserToken(userToken);
        Integer updateRow = sysOrderService.deliverGoods(orderNo);
        UserLoginContext.sysClean();
        if (updateRow == null) {
            throw new ServiceException("订单服务繁忙");
        }
        if (updateRow <= 0) {
            throw new ServiceException("订单发货失败");
        }
        return R.ok("发货成功");
    }

}