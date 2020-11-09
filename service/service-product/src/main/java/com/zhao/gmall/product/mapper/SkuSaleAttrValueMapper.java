package com.zhao.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhao.gmall.model.product.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spuId 查询map 集合数据
     * @param spuId
     * @return
     */
    List<Map> selectSaleAttrValuesBySpu(Long spuId);

}
