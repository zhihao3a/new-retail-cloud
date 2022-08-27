package com.zhihao.newretail.user.feign;

import com.zhihao.newretail.api.user.vo.UserAddressApiVO;
import com.zhihao.newretail.security.context.UserLoginContext;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAddressFeignClient {

    @Autowired
    private UserAddressService userAddressService;

    @RequiresLogin
    @GetMapping("/api/address/list")
    public List<UserAddressApiVO> listUserAddressApiVOS() {
        Integer userId = UserLoginContext.getUserLoginInfo().getUserId();
        List<UserAddressApiVO> userAddressApiVOList = userAddressService.listUserAddressApiVOS(userId);
        UserLoginContext.clean();
        return userAddressApiVOList;
    }

    @GetMapping("/api/address/{addressId}")
    public UserAddressApiVO getUserAddressApiVO(@PathVariable Integer addressId) {
        return userAddressService.getUserAddressApiVO(addressId);
    }

}