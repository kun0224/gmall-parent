package com.zhao.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhao.gmall.list.order.OrderInfo;

/**
 * @Author: zhao
 * @Date: 2020/11/23 19:49
 */
public interface OrderService extends IService<OrderInfo> {

    /**
     * 保存订单
     *
     * @param orderInfo
     * @return
     */
    Long saveOrderInfo(OrderInfo orderInfo);

    /**
     * 生产流水号
     *
     * @param userId
     * @return
     */
    String getTradeNo(String userId);

    /**
     * 比较流水号
     *
     * @param userId      获取缓存中的流水号
     * @param tradeCodeNo 页面传递过来的流水号
     * @return
     */
    boolean checkTradeCode(String userId, String tradeCodeNo);


    /**
     * 删除流水号
     *
     * @param userId
     */
    void deleteTradeNo(String userId);


}
