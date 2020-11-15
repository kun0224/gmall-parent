package com.zhao.gmall.item.service.Impl;

import com.alibaba.fastjson.JSON;
import com.zhao.gmall.item.config.ThreadPoolConfig;
import com.zhao.gmall.item.service.ItemService;
import com.zhao.gmall.model.product.BaseCategoryView;
import com.zhao.gmall.model.product.SkuInfo;
import com.zhao.gmall.model.product.SpuSaleAttr;
import com.zhao.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolConfig;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {

        Map<String, Object> map = new HashMap<>();

        //  获取数据skuInfo + skuImage
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            //存入map中
            map.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolConfig);

        //  获取价格
        CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            //  存储到map中
            map.put("price", skuPrice);
        }, threadPoolConfig);

        //  获取分类
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            //存入map中
            map.put("categoryView",categoryView);
        }, threadPoolConfig);

        //  获取销售属性数据
        CompletableFuture<Void> spuCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.
                    getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            //存入map中
            map.put("spuSaleAttrList",spuSaleAttrListCheckBySku);
        }, threadPoolConfig);


        //  获取销售属性值Id 与 skuId 组成的map
        CompletableFuture<Void> mapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo) -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
            //存入map中
            map.put("valuesSkuJson", valuesSkuJson);
        }, threadPoolConfig);

        //  将数据保存到map key,value {map 中的key 应该如何起名，需要根据页面渲染时获取的key 而定！}
        //  如果页面渲染skuInfo 时， ${skuInfo.skuName} 那么我们的key 就是skuInfo,暂时跟课件保存一致！

        // valuesSkuJson =  {"75|78":"33","75|77":"32"}


//        map.put("skuInfo",skuInfo);
//        map.put("分类",分类集合);
//        ...

        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                skuPriceCompletableFuture,
                categoryViewCompletableFuture,
                spuCompletableFuture,
                mapCompletableFuture
        ).join();

        return map;
    }
}
