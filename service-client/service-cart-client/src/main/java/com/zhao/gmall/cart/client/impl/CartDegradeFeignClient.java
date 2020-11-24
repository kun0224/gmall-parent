package com.zhao.gmall.cart.client.impl;

import com.zhao.gmall.cart.client.CartFeignClient;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.cart.CartInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartDegradeFeignClient implements CartFeignClient {
    @Override
    public Result addToCart(Long skuId, Integer skuNum) {
        return null;
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        return null;
    }
}
