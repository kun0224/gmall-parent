package com.zhao.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.product.BaseSaleAttr;
import com.zhao.gmall.list.product.SpuInfo;
import com.zhao.gmall.product.service.BaseSaleAttrService;
import com.zhao.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台SPU数据接口测试")
@RestController
@RequestMapping("admin/product")
public class SpuManageController {

    @Autowired
    private ManageService manageService;

    @Autowired
    private BaseSaleAttrService baseSaleAttrService;




    /**
     * 分页
     * // http://api.gmall.com/admin/product/{page}/{limit}?category3Id=61
     *
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable Long page, @PathVariable Long limit, SpuInfo spuInfo) {
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        IPage<SpuInfo> spuInfoList = manageService.getSpuInfoPage(spuInfoPage, spuInfo);
        return Result.ok(spuInfoList);
    }

    /**
     * 获取销售属性集合
     * http://api.gmall.com/admin/product/baseSaleAttrList
     * @return
     */
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    /**
     * 添加spu
     * http://api.gmall.com/admin/product/saveSpuInfo
     */
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }





}
