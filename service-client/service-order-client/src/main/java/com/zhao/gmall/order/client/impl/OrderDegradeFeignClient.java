package com.zhao.gmall.order.client.impl;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: zhao
 * @Date: 2020/11/23 18:57
 */
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {
    @Override
    public Result<Map<String, Object>> trade() {
        return null;
    }
}
