package com.zhao.gmall.all.controller;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @Author: zhao
 * @Date: 2020/11/23 19:37
 */
@Controller
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;



    /**
     * 确认订单
     *
     * @param model
     * @return
     */
    @GetMapping("trade.html")
    public String trade(Model model) {
        Result<Map<String, Object>> result = orderFeignClient.trade();

        model.addAllAttributes(result.getData());
        return "order/trade";
    }
}

