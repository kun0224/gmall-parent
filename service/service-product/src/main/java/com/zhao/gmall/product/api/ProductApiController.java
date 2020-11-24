package com.zhao.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.product.*;
import com.zhao.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    ManageService manageService;

    /**
     * 根据skuid获取sku信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getAttrValueList(@PathVariable Long skuId) {
        SkuInfo skuInfo = manageService.getSkuIngfo(skuId);
        return skuInfo;
    }

    /**
     * 通过三级分类id查询分类信息
     *
     * @return
     */
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        return manageService.getCategoryViewByCategory3Id(category3Id);
    }

    /**
     * 获取sku最新价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId) {
        return manageService.getSkuPrice(skuId);
    }

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId) {
        return manageService.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    /**
     * 根据spuId 查询map 集合数据
     *
     * @param spuId
     * @return
     */
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId) {

        return manageService.getSkuValueIdsMap(spuId);
    }

    /**
     * 获取全部分类信息
     *
     * @return
     */
    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList() {
        List<JSONObject> list = manageService.getBaseCategoryList();
        return Result.ok(list);
    }

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId")Long tmId){
        // BaseTrademark baseTrademark = baseTrademarkService.getById(tmId);
        return manageService.getTrademarkByTmId(tmId);
    }

    //  根据skuId 获取平台属性+平台属性值
    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable Long skuId){
        return manageService.getAttrList(skuId);
    }


}
