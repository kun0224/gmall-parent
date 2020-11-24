package com.zhao.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhao.gmall.list.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    //  根据分类Id 查询平台属性数据集合
    public List<BaseAttrInfo> selectBaseAttrInfoList(@Param("category1Id") Long category1Id,
                                                     @Param("category2Id") Long category2Id,
                                                     @Param("category3Id") Long category3Id);

    //  根据skuId 查询平台属性+平台属性值
    List<BaseAttrInfo> selectAttrList(Long skuId);
}
