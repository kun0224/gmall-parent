package com.zhao.gmall.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: zhao
 * @Date: 2020/11/24 16:27
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.zhao.gmall"})
@EnableDiscoveryClient
public class ServiceMqApplication {

   public static void main(String[] args) {
      SpringApplication.run(ServiceMqApplication.class, args);
   }

}

