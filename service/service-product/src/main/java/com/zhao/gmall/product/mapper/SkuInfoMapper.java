package com.zhao.gmall.product.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhao.gmall.model.product.SkuImage;
import com.zhao.gmall.model.product.SkuInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
}
