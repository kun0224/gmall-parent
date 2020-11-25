package com.zhao.gmall.task.scheduled;

import com.zhao.gmall.common.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: zhao
 * @Date: 2020/11/25 16:56
 */
@Component
@EnableScheduling
@Slf4j
public class ScheduledTask {

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void task1(){
        //rabbitService.sendMessage()
    }

}
