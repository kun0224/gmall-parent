package com.zhao.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    //  制作线程池
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        //  new ThreadPoolExecutor
        //  核心线程池数跟你的硬件有关系！
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,
                50,
                5L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5)

        );
        return threadPoolExecutor;
    }
}
