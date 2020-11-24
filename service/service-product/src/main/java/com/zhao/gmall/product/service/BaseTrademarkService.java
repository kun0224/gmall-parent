package com.zhao.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhao.gmall.list.product.BaseTrademark;


public interface BaseTrademarkService extends IService<BaseTrademark> {

    /**
    * Banner分页列表
    * @param pageParam
    * @return
    */
   IPage<BaseTrademark> selectPage(Page<BaseTrademark> pageParam);


}
