package com.zhao.gmall.user.controller;

import com.zhao.gmall.list.user.UserAddress;
import com.zhao.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zhao
 * @Date: 2020/11/23 18:29
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户地址
     *
     * @param userId
     * @return
     */
    @GetMapping("inner/findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") String userId) {
        return userAddressService.findUserAddressListByUserId(userId);
    }

}

