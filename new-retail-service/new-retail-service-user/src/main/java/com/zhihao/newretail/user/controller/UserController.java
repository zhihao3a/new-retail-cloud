package com.zhihao.newretail.user.controller;

import com.zhihao.newretail.core.util.R;
import com.zhihao.newretail.security.context.UserLoginContext;
import com.zhihao.newretail.security.annotation.RequiresLogin;
import com.zhihao.newretail.user.pojo.dto.UserRegisterDTO;
import com.zhihao.newretail.user.pojo.vo.UserInfoVO;
import com.zhihao.newretail.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public R register(@RequestBody UserRegisterDTO userRegisterDTO) {
        Integer registerRow = userService.insertUser(userRegisterDTO);

        if (!ObjectUtils.isEmpty(registerRow)) {
            return R.ok("注册成功");
        }
        return R.error("注册失败");
    }

    @RequiresLogin
    @GetMapping("/userInfo")
    public R userInfo() {
        Integer userId = UserLoginContext.getUserLoginInfo().getUserId();
        UserInfoVO userInfoVO = userService.getUserInfoVO(userId);
        UserLoginContext.clean();

        return R.ok().put("data", userInfoVO);
    }

}
