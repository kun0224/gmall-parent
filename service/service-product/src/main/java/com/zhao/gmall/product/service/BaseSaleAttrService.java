package com.zhao.gmall.product.service;

import com.zhao.gmall.list.product.BaseSaleAttr;

import java.util.List;

public interface BaseSaleAttrService {

    /**
     * 查询所有的销售属性数据
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

}
