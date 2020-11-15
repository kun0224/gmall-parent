package com.zhao.gmall.common.cache;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})   //注解使用范围
@Retention(RetentionPolicy.RUNTIME)    //注解的生命周期
public @interface GmallCache {

    //定义一个组成缓存中的key的前缀（查出的数据要放的redis缓存中，缓存要有key）
    //使用方式：@GmallCache(prefix = "sku"),如果括号中不填，默认是"key"
    String prefix() default "key";
}
