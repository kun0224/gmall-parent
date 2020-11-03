package com.zhao.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhao.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    public List<BaseAttrInfo> selectBaseAttrInfoList(@Param("category1Id") Long category1Id,
                                                     @Param("category2Id") Long category2Id,
                                                     @Param("category3Id") Long category3Id);
}
