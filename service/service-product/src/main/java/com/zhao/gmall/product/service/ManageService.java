package com.zhao.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ManageService {

    /**
     * 查询所有的一级分类信息
     *
     * @return
     */
    List<BaseCategory1> getCategory1();

    /**
     * 根据一级分类Id 查询二级分类数据
     *
     * @param category1Id
     * @return
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级分类Id 查询三级分类数据
     *
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 根据分类Id 获取平台属性数据
     * 接口说明：
     * 1，平台属性可以挂在一级分类、二级分类和三级分类
     * 2，查询一级分类下面的平台属性，传：category1Id，0，0；   取出该分类的平台属性
     * 3，查询二级分类下面的平台属性，传：category1Id，category2Id，0；
     * 取出对应一级分类下面的平台属性与二级分类对应的平台属性
     * 4，查询三级分类下面的平台属性，传：category1Id，category2Id，category3Id；
     * 取出对应一级分类、二级分类与三级分类对应的平台属性
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存平台属性的方法
     *
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性Id 查询平台属性对象
     *
     * @param attrId
     * @return
     */
    BaseAttrInfo getBaseAttrInfo(Long attrId);


    /**
     * spu分页查询
     * 因为SpuInfo 中包含三级分类id，所有直接将spuinfo传入第二个参数
     *
     * @param pageParam
     * @param spuInfo
     * @return
     */
    IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo);


    /**
     * 添加spu
     *
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId获取图片列表
     *
     * @param spuId
     */
    List<SpuImage> getspuImageList(Long spuId);

    /**
     * 根据spuId 查询销售属性集合
     *
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 添加sku
     *
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * sku分页
     *
     * @param skuInfoPage
     * @return
     */
    IPage<SkuInfo> getPage(Page<SkuInfo> skuInfoPage);

    /**
     * 商品上架
     *
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 商品下架
     *
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     * 根据skuid获取sku信息
     *
     * @param skuId
     * @return
     */
    SkuInfo getSkuIngfo(Long skuId);

    /**
     * 通过三级分类id查询分类信息
     *
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    /**
     * 获取sku最新价格
     *
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     * 根据spuId 查询map 集合数据
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);

    /**
     * 获取全部分类信息
     * @return
     */
    List<JSONObject> getBaseCategoryList();
}
