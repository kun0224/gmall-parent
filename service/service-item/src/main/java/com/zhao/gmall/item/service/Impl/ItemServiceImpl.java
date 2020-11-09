package com.zhao.gmall.item.service.Impl;

import com.zhao.gmall.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Override
    public Map<String, Object> getBySkuId(Long skuId) {
        Map<String,Object> result = new HashMap<>();
        return result;
    }
}
