package com.zhao.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.zhao.gmall.common.constant.RedisConst;
import jdk.nashorn.internal.ir.IfNode;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 环绕通知
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    //@Around是环绕通知，要填写切入点，（切GmallCache注解）
    //注解要标注到不同的方法，不同的方法有不同的返回值，所以用父类Object
    //@Around是环绕通知的参数是固定的---ProceedingJoinPoint
    @SneakyThrows
    @Around("@annotation(com.zhao.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {
        //声明一个对象
        Object object = new Object();

        //在环绕通知中处理业务逻辑（实现分布式锁）
        //获取到注解，注解使用在方法上，通过注解来判断哪个方法要使用分布式锁
        MethodSignature signature = (MethodSignature) point.getSignature();
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);

        //获取到注解上的前缀
        String prefix = gmallCache.prefix();

        //获取方法上的传参(方法上不知道要传几个参数，所以将参数存入数组中)
        Object[] args = point.getArgs();

        //组成缓存的key，需要前缀+方法传入的参数（保证key的唯一，防止重复）
        //Arrays.asList(args).toString()将数组转成List集合并转成字符串
        String key = prefix + Arrays.asList(args).toString();

        //  防止redis ，redisson 出现问题！

        try {
            //从缓存中获取数据，
            // 类似于skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            object = cacheHit(key, signature);

            if (object == null) {
                //从数据库中获取数据，并放入缓存，防止缓存击穿必须上锁
                String lockKey = prefix + ":lock";
                //准备上锁
                RLock lock = redissonClient.getLock(lockKey);
                boolean result = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);

                //上锁成功
                if (result) {
                    try {
                        //执行方法体
                        //point.getArgs()获取方法上的传参
                        object = point.proceed(point.getArgs());

                        //判断object是否为空
                        if (object == null) {
                            //防止缓存穿透
                            Object object1 = new Object();
                            //JSON.toJSONString(object1)将Object转换成json
                            redisTemplate.opsForValue().set(key, JSON.toJSONString(object1), RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);

                            //返回数据
                            return object1;
                        }

                        redisTemplate.opsForValue().set(key, JSON.toJSONString(object), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);

                        //返回数据
                        return object;
                    } finally {
                        lock.unlock();
                    }

                } else {
                    //上锁失败,睡眠自旋
                    Thread.sleep(1000);
                    return cacheAroundAdvice(point);

                }
            } else {
                return object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //  如果出现问题数据库兜底
        return point.proceed(point.getArgs());
    }


    /**
     * 表示从缓存中获取数据
     *
     * @param key       缓存的key
     * @param signature 获取方法的返回值类型
     * @return
     */
    private Object cacheHit(String key, MethodSignature signature) {
        //  通过key 来获取缓存的数据
        String strJson = (String) redisTemplate.opsForValue().get(key);
        //  表示从缓存中获取到了数据
        if (!StringUtils.isEmpty(strJson)) {
            //  字符串存储的数据是什么?   就是方法的返回值类型
            Class returnType = signature.getReturnType();
            //  将字符串变为当前的返回值类型
            return JSON.parseObject(strJson, returnType);
        }
        return null;
    }

}
