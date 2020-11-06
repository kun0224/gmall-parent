package com.zhao.gmall.product.service;

import com.zhao.gmall.model.product.BaseSaleAttr;
import com.zhao.gmall.product.mapper.BaseSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface BaseSaleAttrService {

    /**
     * 查询所有的销售属性数据
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

}
