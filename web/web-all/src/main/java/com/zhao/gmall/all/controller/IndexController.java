package com.zhao.gmall.all.controller;

import com.zhao.gmall.common.result.Result;
import com.zhao.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @RequestMapping({"/","index.html"})
    public String index(Model model){
        Result result = productFeignClient.getBaseCategoryList();
        //  后台需要存储一个list
        model.addAttribute("list",result.getData());
        //  返回首页视图
        return "index/index";
    }
}

