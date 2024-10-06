package com.rupak.controller;

import com.rupak.annotations.Autowired;
import com.rupak.annotations.RestController;
import com.rupak.service.ProductService;
import lombok.Getter;
import lombok.Setter;

@RestController
@Getter
@Setter
public class ProductController {

    @Autowired
    private ProductService productService;

    public String addProduct(String name) {
        return productService.addProduct(name);
    }

    public String getProduct(String productId) {
        return productService.getProduct(productId);
    }
}
