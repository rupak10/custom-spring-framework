package com.rupak.service;

import com.rupak.annotations.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProductService {

    private Map<String, String> map;

    public ProductService() {
        map = new HashMap<>();
    }

    public String addProduct(String name) {
        String productId = UUID.randomUUID().toString();
        map.put(productId, name);
        return productId;
    }

    public String getProduct(String productId) {
        return map.getOrDefault(productId, null);
    }
}
