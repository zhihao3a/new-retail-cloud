package com.zhihao.newretail.auth.service;

import com.zhihao.newretail.auth.form.UserLoginForm;
import com.zhihao.newretail.core.util.R;

public interface LoginService {

    /*
    * 用户登录(账号密码)
    * */
    R login(UserLoginForm form);

    /*
    * 后台管理系统用户登录
    * */
    R loginAdmin(UserLoginForm form);

}
