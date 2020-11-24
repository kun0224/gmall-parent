package com.zhao.gmall.cart.service;


import com.zhao.gmall.list.cart.CartInfo;

import java.util.List;

public interface CartService {


    /**
     * 添加购物车，用户id,商品id，商品数量。
     *
     * @param skuId
     * @param userId
     * @param skuNum
     */
    void addToCart(Long skuId, String userId, Integer skuNum);

    /**
     * 查询购物车列表
     *
     * @param userId
     * @param userTempId
     * @return
     */
    List<CartInfo> getCartList(String userId, String userTempId);

    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    List<CartInfo> getCartCheckedList(String userId);


}
