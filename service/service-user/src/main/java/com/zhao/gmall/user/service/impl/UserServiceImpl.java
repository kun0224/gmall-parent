package com.zhao.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhao.gmall.list.user.UserInfo;
import com.zhao.gmall.user.mapper.UserInfoMapper;
import com.zhao.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户先访问web 应用时，先检查 cookie 中是否有token ！
 * true:
 * 存在，登录！ 【有可能已经登录了{可以手动将redis 删除！token 保留！}。】
 * false:
 * 没有登录，跳转到登录页面，或者提示用户注册。【{手动将token 从cookie 中删除！但是在redis 中已经存在了用户！}】
 * <p>
 * a.	select * from user_info where username = ? and password = ?
 * <p>
 * userInfo!=null 登录成功！
 * <p>
 * 携带着token 回跳到当前应用 ，此时是不是需要将token 写入cookie！
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {

        //密码加密
        String passwd = userInfo.getPasswd();
        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passwd",newPasswd)
                .eq("login_name", userInfo.getLoginName());
        UserInfo info = userInfoMapper.selectOne(queryWrapper);
        if (info != null){
            return info;
        }
        return null;
    }
}
