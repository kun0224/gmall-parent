package com.zhao.gmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhao.gmall.list.user.UserAddress;

import java.util.List;

/**
 *
 */
public interface UserAddressService extends IService<UserAddress> {
    /**
     * 根据用户Id 查询用户的收货地址列表！
     *
     * @param userId
     * @return
     */
    List<UserAddress> findUserAddressListByUserId(String userId);

}
