package com.zhao.gmall.item.client.impl;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

@Component
public class ItemDegradeFeignClient implements ItemFeignClient {

    @Override
    public Result getItem(Long skuId) {
        return null;
    }
}
