package com.zhao.gmall.list.service;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.list.SearchParam;
import com.zhao.gmall.list.list.SearchResponseVo;

public interface SearchService {

    //  完成商品的上架
    void upperGoods(Long skuId);

    //  完成商品的下架
    void lowerGoods(Long skuId);

    //  定义更es 中hotScore 热度排名 参数一定需要skuId ; Result | void
    Result incrHotcore(Long skuId);

    //  检索接口
    SearchResponseVo search(SearchParam searchParam) throws Exception;

}

