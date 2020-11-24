package com.zhao.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhao.gmall.common.constant.RedisConst;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.common.util.IpUtil;
import com.zhao.gmall.list.user.UserInfo;
import com.zhao.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 单点登录
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @param userInfo
     * @param request
     * @param response
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {
        UserInfo login = userService.login(userInfo);
        if (login !=null){
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            HashMap<String,Object> map = new HashMap<>();
            map.put("nuckName", login.getNickName());
            map.put("token", token);

            JSONObject userJson = new JSONObject();
            userJson.put("userId", login.getId().toString());
            userJson.put("ip", IpUtil.getIpAddress(request));
            //缓存中添加用户账号信息
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token, userJson.toJSONString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            return Result.ok(map);

        }else {
            return Result.fail().message("用户名或密码错误");
        }
    }


    /**
     * 退出
     * @param request
     * @return
     */
    @GetMapping("logout")
    public Result logout(HttpServletRequest request){

        //  将缓存中的userInfo 信息删除即可！
        String token = request.getHeader("token");

        //  key = RedisConst.USER_LOGIN_KEY_PREFIX + token  user:login:token
        String userKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;
        redisTemplate.delete(userKey);
        return Result.ok();
    }


}
