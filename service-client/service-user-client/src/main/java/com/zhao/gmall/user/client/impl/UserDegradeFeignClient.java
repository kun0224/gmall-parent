package com.zhao.gmall.user.client.impl;

import com.zhao.gmall.list.user.UserAddress;
import com.zhao.gmall.user.client.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: zhao
 * @Date: 2020/11/23 18:32
 */
@Component
public class UserDegradeFeignClient implements UserFeignClient {
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return null;
    }
}
