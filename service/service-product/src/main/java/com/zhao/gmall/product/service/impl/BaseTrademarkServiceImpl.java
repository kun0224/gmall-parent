package com.zhao.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhao.gmall.model.product.BaseTrademark;
import com.zhao.gmall.product.mapper.BaseTrademarkMapper;
import com.zhao.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    /**
     * 品牌详情分页
     *
     * @param pageParam
     * @return
     */
    @Override
    public IPage<BaseTrademark> selectPage(Page<BaseTrademark> pageParam) {

        IPage<BaseTrademark> trademarkIPage = baseTrademarkMapper.selectPage(pageParam, null);
        return trademarkIPage;
    }
}
