package com.zhao.gmall.cart.service.impl;

import com.zhao.gmall.cart.mapper.CartInfoMapper;
import com.zhao.gmall.cart.service.CartAsyncService;
import com.zhao.gmall.list.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CartAsyncServiceImpl implements CartAsyncService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    /**
     *修改购物车
     * @param cartInfo
     */
    @Async
    @Override
    public void updateCartInfo(CartInfo cartInfo) {
        cartInfoMapper.updateById(cartInfo);
    }

    /**
     *保存购物车
     * @param cartInfo
     */
    @Async
    @Override
    public void saveCartInfo(CartInfo cartInfo) {
        cartInfoMapper.insert(cartInfo);
    }
}
