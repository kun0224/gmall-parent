package com.zhao.gmall.item.service.Impl;

import com.alibaba.fastjson.JSON;
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

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {

        Map<String, Object> map = new HashMap<>();
        //  获取数据skuInfo + skuImage
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        //  获取价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        //  获取分类
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        //  获取销售属性数据
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());

        //  获取销售属性值Id 与 skuId 组成的map
        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        String valuesSkuJson = JSON.toJSONString(skuValueIdsMap);
        System.out.println(valuesSkuJson);
        //  将数据保存到map key,value {map 中的key 应该如何起名，需要根据页面渲染时获取的key 而定！}
        //  如果页面渲染skuInfo 时， ${skuInfo.skuName} 那么我们的key 就是skuInfo,暂时跟课件保存一致！
        map.put("skuInfo",skuInfo);
        map.put("categoryView",categoryView);
        map.put("price",skuPrice);
        // valuesSkuJson =  {"75|78":"33","75|77":"32"}
        map.put("valuesSkuJson", valuesSkuJson);
        map.put("spuSaleAttrList",spuSaleAttrListCheckBySku);

//        map.put("skuInfo",skuInfo);
//        map.put("分类",分类集合);
//        ...
        return map;
    }
}
