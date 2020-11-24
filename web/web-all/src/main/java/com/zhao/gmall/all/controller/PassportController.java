package com.zhao.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户认证接口
 */
@Controller
public class PassportController {

    //  编写控制器
    //  window.location.href = 'http://passport.gmall.com/login.html?originUrl='+window.location.href
    @GetMapping("login.html")
    public String login(HttpServletRequest request){
        //  前台页面需要的 originUrl: [[${originUrl}]],// 后台存储这么一个originUrl
        String originUrl = request.getParameter("originUrl");
        //  存储跳转的url!
        request.setAttribute("originUrl",originUrl);
        return "login";
    }

}
