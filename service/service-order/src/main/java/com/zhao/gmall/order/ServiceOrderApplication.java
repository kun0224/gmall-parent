package com.zhao.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: zhao
 * @Date: 2020/11/23 18:47
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.zhao.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.zhao.gmall")
public class ServiceOrderApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServiceOrderApplication.class,args);
    }
}

