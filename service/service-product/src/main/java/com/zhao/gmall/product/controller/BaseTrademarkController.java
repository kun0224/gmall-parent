package com.zhao.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.list.product.BaseTrademark;
import com.zhao.gmall.product.service.BaseTrademarkService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台品牌管理数据接口")
@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    /**
     * 后台品牌分页
     * http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}
     *
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result getTrademarkList(@PathVariable Long page, @PathVariable Long limit) {
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkService.selectPage(baseTrademarkPage);
        return Result.ok(baseTrademarkIPage);
    }

    /**
     * http://api.gmall.com/admin/product/baseTrademark/save
     */
    @PostMapping("save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * http://api.gmall.com/admin/product/baseTrademark/update
     */
    @PutMapping("update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * http://api.gmall.com/admin/product/baseTrademark/remove/{id}
     */
    @DeleteMapping("remove/{id}")
    public Result removeBaseTrademark(@PathVariable Long id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    /**
     * http://api.gmall.com/admin/product/baseTrademark/get/{id}
     */
    @GetMapping("get/{id}")
    public Result getBaseTrademark(@PathVariable Long id) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * 品牌回显
     * http://api.gmall.com/admin/product/baseTrademark/getTrademarkList
     */
    @GetMapping("getTrademarkList")
    public Result<List<BaseTrademark>> getTrademarkList(){
        List<BaseTrademark> baseTrademarkList = baseTrademarkService.list(null);
        return Result.ok(baseTrademarkList);
    }
}
