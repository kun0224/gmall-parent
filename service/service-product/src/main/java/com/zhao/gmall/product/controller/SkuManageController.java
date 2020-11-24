package com.zhao.gmall.product.controller;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.product.SkuInfo;
import com.zhao.gmall.list.product.SpuImage;
import com.zhao.gmall.list.product.SpuSaleAttr;
import com.zhao.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品sku接口")
@RestController
@RequestMapping("admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;

    /**
     * 根据spuId获取图片列表
     * http://api.gmall.com/admin/product/spuImageList/{spuId}
     */
    @GetMapping("spuImageList/{spuId}")
    public Result<List<SpuImage>> spuImageList(@PathVariable Long spuId) {
        List<SpuImage> spuImageList = manageService.getspuImageList(spuId);
        return Result.ok(spuImageList);
    }

    /**
     * 根据spuId 查询销售属性集合
     * http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
     *
     * @return
     */
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result<List<SpuSaleAttr>> spuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = manageService.spuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrList);
    }


    /**
     * 添加sku
     * http://api.gmall.com/admin/product/saveSkuInfo
     */
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }


}
