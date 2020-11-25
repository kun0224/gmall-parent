package com.zhao.gmall.mq.controller;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.common.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: zhao
 * @Date: 2020/11/25 17:58
 */
@RestController
@RequestMapping("/Mq")
@Slf4j
public class MqController {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 消息发送
     * @return
     */
    @GetMapping("sendConfirm")
    public Result sendConfirm(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitService.sendMessage("exchange.confirm", "routing.confirm","ok----");
        return Result.ok();
    }
}
