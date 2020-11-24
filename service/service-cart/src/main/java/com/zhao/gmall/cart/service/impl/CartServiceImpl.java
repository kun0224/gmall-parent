package com.zhao.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhao.gmall.cart.mapper.CartInfoMapper;
import com.zhao.gmall.cart.service.CartAsyncService;
import com.zhao.gmall.cart.service.CartService;
import com.zhao.gmall.common.constant.RedisConst;
import com.zhao.gmall.common.util.DateUtil;
import com.zhao.gmall.list.cart.CartInfo;
import com.zhao.gmall.list.product.SkuInfo;
import com.zhao.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CartAsyncService cartAsyncService;

    /**
     * 添加到购物车
     *
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {

        String cartkey = getCartKey(userId);

        CartInfo cartInfo = new CartInfo();

        try {
            //先判断缓存中是否有cartkey，先加载数据库中的数据放入缓存
            if (!redisTemplate.hasKey(cartkey)) {
                this.loadCartCache(userId);
            }

            //  当 this.loadCartCache(userId); 执行的时候，说明缓存中一定有数据了！在此查询缓存！
            //  当 this.loadCartCache(userId); 这段代码没有执行 ，    缓存中有数据！
            //  hget(key,field); user:userId:cart    field=skuId,  value=cartInfo.toString();
            cartInfo = (CartInfo) redisTemplate.boundHashOps(cartkey).get(skuId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            //如果redis缓存异常，执行从数据库获取数据
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("sku_id", skuId);
            cartInfo = cartInfoMapper.selectOne(queryWrapper);
        }

        //  查询数据库,select * from cart_info where user_id = userId and sku_id = skuId;
//        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId).eq("sku_id", skuId);
//        CartInfo cartInfo = cartInfoMapper.selectOne(queryWrapper);

        //  1、添加购物车的时候，需要判断购物车中是否有该商品
        if (cartInfo != null) {
            //购物车中有当前商品
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);

            //获取到商品的实时价格给skuprice
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(skuId));

            //赋值更新时间
            cartInfo.setUpdateTime(new Timestamp(new Date().getTime()));

            // 修改数据库
            //cartInfoMapper.updateById(cartInfo);
            cartAsyncService.updateCartInfo(cartInfo);

        } else {
            //购物车中没有商品
            CartInfo cartInfo1 = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuNum(skuNum);
            cartInfo1.setSkuId(skuId);
            cartInfo1.setUserId(userId);
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setUpdateTime(new Timestamp(new Date().getTime()));
            cartInfo1.setCreateTime(new Timestamp(new Date().getTime()));

            //添加数据库
            //cartInfoMapper.insert(cartInfo1);
            cartAsyncService.saveCartInfo(cartInfo1);
            cartInfo = cartInfo1;
        }

        //redis中添加该商品缓存
        redisTemplate.opsForHash().put(cartkey, skuId.toString(), cartInfo);

        //给缓存添加过期时间
        setCartKeyExpire(cartkey);

    }

    /**
     * 查询购物车列表
     *
     * @param userId
     * @param userTempId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {

        List<CartInfo> cartInfoList = new ArrayList<>();

        //userId为null，未登录
        if (StringUtils.isEmpty(userId)) {
            cartInfoList = this.getCartList(userTempId);
            return cartInfoList;
        }

        //userId不为null，用户已登录
        if (!StringUtils.isEmpty(userId)) {
            cartInfoList = this.getCartList(userId);
        }
        return cartInfoList;
    }

    /**
     * 根据用户Id 查询购物车物品列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        // 定义key user:userId:cart
        String cartKey = this.getCartKey(userId);
        List<CartInfo> cartCachInfoList = redisTemplate.opsForHash().values(cartKey);
        if (null != cartCachInfoList && cartCachInfoList.size() > 0) {
            for (CartInfo cartInfo : cartCachInfoList) {
                // 获取选中的商品！
                if (cartInfo.getIsChecked().intValue() == 1) {
                    cartInfoList.add(cartInfo);
                }
            }
        }
        return cartInfoList;

    }


    /**
     * 根据用户查询购物车列表
     *
     * @param userId
     * @return
     */
    public List<CartInfo> getCartList(String userId) {
        //声明一个返回的集合对象
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }

        //根据用户Id查询（先查缓存，缓存没有，再查询数据库）
        String cartKey = this.getCartKey(userId);
        //缓存中储存的value 都是cartInfo
        cartInfoList = redisTemplate.opsForHash().values(cartKey);
        //cartInfoList = redisTemplate.boundHashOps(cartKey).values();
        if (!StringUtils.isEmpty(cartInfoList)) {
            //查询结果不为空
            //有排序规则？
            cartInfoList.sort(new Comparator<CartInfo>() {
                //自定义比较器
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return DateUtil.truncatedCompareTo(o2.getUpdateTime(), o1.getUpdateTime(), Calendar.SECOND);
                }
            });
            return cartInfoList;
        } else {
            //缓存中没有数据
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }
    }

    /**
     * 缓存没有时，在数据库中查询购物车列表
     *
     * @param userId
     * @return
     */
    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        cartInfoList = cartInfoMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", userId));

        //判断
        if (StringUtils.isEmpty(cartInfoList)) {
            //cartInfoList返回空
            return cartInfoList;
        }

        //如果cartInfoList不为空，将数据存入缓存
        String cartKey = getCartKey(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            //给实时价格赋值
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));

            hashMap.put(cartInfo.getSkuId().toString(), cartInfo);
        }
        //将数据存入缓存
        redisTemplate.opsForHash().putAll(cartKey, hashMap);

        //给缓存一个过期时间
        this.setCartKeyExpire(cartKey);

        //排序
        cartInfoList.sort(new Comparator<CartInfo>() {
            //自定义比较器
            @Override
            public int compare(CartInfo o1, CartInfo o2) {
                return DateUtil.truncatedCompareTo(o2.getUpdateTime(), o1.getUpdateTime(), Calendar.SECOND);
            }
        });

        //返回数据
        return cartInfoList;
    }


    /**
     * 设置缓存过期时间
     *
     * @param cartkey
     */
    private void setCartKeyExpire(String cartkey) {
        redisTemplate.expire(cartkey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }


    /**
     * 获取购物车的key
     *
     * @param userId
     * @return
     */
    private String getCartKey(String userId) {
        String cartkey = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        return cartkey;
    }
}
