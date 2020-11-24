package com.zhao.gmall.product.service.impl;

import com.zhao.gmall.list.product.BaseSaleAttr;
import com.zhao.gmall.product.mapper.BaseSaleAttrMapper;
import com.zhao.gmall.product.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseSaleAttrServiceImpl implements BaseSaleAttrService {

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    /**
     * 查询所有的销售属性数据
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }
}
