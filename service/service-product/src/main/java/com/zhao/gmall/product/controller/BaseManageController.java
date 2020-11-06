package com.zhao.gmall.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.regexp.internal.RE;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.model.product.*;
import com.zhao.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台数据接口测试")
@RestController
@RequestMapping("admin/product")
//@CrossOrigin
public class BaseManageController {

    @Autowired
    private ManageService manageService;

    /**
     * 查询所有的一级分类信息
     */
    @GetMapping("getCategory1")
    public Result<List<BaseCategory1>> getCategory1() {
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    /**
     * 根据一级分类Id 查询二级分类数据
     *
     * @return
     */
    @GetMapping("getCategory2/{categoryId}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable("categoryId") Long categoryId) {
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(categoryId);
        return Result.ok(baseCategory2List);
    }

    /**
     * 根据二级分类Id 查询三级分类数据
     *
     * @param category2Id
     * @return
     */
    @GetMapping("getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable("category2Id") Long category2Id) {
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    /**
     * 根据分类Id获取平台属性数据
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> attrInfoList(@PathVariable("category1Id") Long category1Id,
                                                   @PathVariable("category2Id") Long category2Id,
                                                   @PathVariable("category3Id") Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * 保存平台属性方法
     * //  http://api.gmall.com/admin/product/saveAttrInfo
     * //  保存平台属性
     * //  前台传递的json 数据，转化为对象，那么这个对象就是BaseAttrInfo;
     * //  springMVC || @ResponseBody {将java 对象转化为Json 字符串，直接将数据输出到页面}
     * //  @RequestBody {将 Json 字符串转化为Java Object }
     * //  即使保存，又是修改的控制器！
     *
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据平台属性Id获取平台属性对象数据
     *
     * @param attrId
     * @return
     */
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId) {
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        return Result.ok(baseAttrInfo.getAttrValueList());
    }

    /**
     * sku分页
     * http://api.gmall.com/admin/product/list/1/10
     */
    @GetMapping("list/{page}/{limit}")
    public Result skuPage(@PathVariable Long page, @PathVariable Long limit) {
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        IPage<SkuInfo> skuInfoIPage = manageService.getPage(skuInfoPage);
        return Result.ok(skuInfoIPage);
    }

    /**
     * 商品上架
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        manageService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 商品下架
     * @return
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        manageService.cancelSale(skuId);
        return Result.ok();
    }


}
