package com.zhao.gmall.user.service;


import com.zhao.gmall.list.user.UserInfo;

public interface UserService {

    /**
     * 登录方法
     * @return
     */
    UserInfo login(UserInfo userInfo);

}
