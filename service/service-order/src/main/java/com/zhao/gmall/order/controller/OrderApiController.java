package com.zhao.gmall.order.controller;

import com.zhao.gmall.cart.client.CartFeignClient;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.common.util.AuthContextHolder;
import com.zhao.gmall.list.cart.CartInfo;
import com.zhao.gmall.list.order.OrderDetail;
import com.zhao.gmall.list.order.OrderInfo;
import com.zhao.gmall.list.user.UserAddress;
import com.zhao.gmall.order.service.OrderService;
import com.zhao.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhao
 * @Date: 2020/11/23 18:54
 */
@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private OrderService orderService;


    /**
     * 确认订单
     *
     * @param request
     * @return
     */
    @GetMapping("auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        // 获取到用户Id
        String userId = AuthContextHolder.getUserId(request);

        //获取用户地址
        List<UserAddress> userAddressList = userFeignClient.findUserAddressListByUserId(userId);

        // 渲染送货清单
        // 先得到用户想要购买的商品！
        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);
        // 声明一个集合来存储订单明细
        ArrayList<OrderDetail> detailArrayList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            //声明一个订单明细对象
            OrderDetail orderDetail = new OrderDetail();
            //赋值
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());

            // 添加到集合
            detailArrayList.add(orderDetail);
        }
        // 计算总金额
        OrderInfo orderInfo = new OrderInfo();
        //  必须将orderDetailList 这个集合赋值。
        orderInfo.setOrderDetailList(detailArrayList);

        orderInfo.sumTotalAmount();

        //  总价格在哪！ orderInfo.getTotalAmount();
        //  这里声明一个map 集合
        Map<String, Object> result = new HashMap<>();

        result.put("userAddressList", userAddressList);
        //  存储送货清单
        result.put("detailArrayList", detailArrayList);
        //  计算件数 两种选择：  一种是算有几种sku , 另一种方式：计算sku的总件数。
        result.put("totalNum", detailArrayList.size());
        result.put("totalAmount", orderInfo.getTotalAmount());

        //  获取流水号并保存
        String tradeNo = orderService.getTradeNo(userId);
        result.put("tradeNo", tradeNo);

        return Result.ok(result);
    }

    /**
     * 提交订单
     *
     * @param orderInfo
     * @param request
     * @return
     */
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request) {
        // 获取到用户Id
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));

        // 获取前台页面的流水号
        String tradeNo = request.getParameter("tradeNo");

        // 调用服务层的比较方法
        boolean flag = orderService.checkTradeCode(userId, tradeNo);
        if (!flag) {
            // 比较失败！
            return Result.fail().message("不能重复提交订单！");
        }
        //  删除流水号
        orderService.deleteTradeNo(userId);

        // 验证通过，保存订单！
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }


}

