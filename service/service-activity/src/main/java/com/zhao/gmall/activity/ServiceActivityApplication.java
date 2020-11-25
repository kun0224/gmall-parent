package com.zhao.gmall.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: zhao
 * @Date: 2020/11/25 16:44
 */
@SpringBootApplication
@ComponentScan({"com.zhao.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.zhao.gmall"})
public class ServiceActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}
