package com.zhao.gmall.item.service;

import java.util.Map;


public interface ItemService {
    Map<String, Object> getBySkuId(Long skuId);
}
