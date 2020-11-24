package com.zhao.gmall.cart.service;

import com.zhao.gmall.list.cart.CartInfo;

public interface CartAsyncService {

    /**
     * 修改购物车
     * @param cartInfo
     */
    void updateCartInfo(CartInfo cartInfo);

    /**
     * 保存购物车
     * @param cartInfo
     */
    void saveCartInfo(CartInfo cartInfo);
}
