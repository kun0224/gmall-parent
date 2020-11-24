package com.zhao.gmall.list.controller;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.list.Goods;
import com.zhao.gmall.list.list.SearchParam;
import com.zhao.gmall.list.list.SearchResponseVo;
import com.zhao.gmall.list.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/list")
public class ListApiController {

    //  引入一个操作es 的客户端
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    //  根据java 代码来自动生产index,type -- mapping！
    @GetMapping("inner/createIndex")
    public Result createIndex(){
        //  执行代码
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);

        return Result.ok();
    }

    //  商品上架
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId){
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    //  商品下架
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId){
        searchService.incrHotcore(skuId);
        return Result.ok();
    }

    //  检索数据的接口发布到 feign 上！ feign 传输对象数据时，这个数据应该是Json
    @PostMapping
    public Result search(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo = null;
        try {
            searchResponseVo = searchService.search(searchParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(searchResponseVo);
    }
}


