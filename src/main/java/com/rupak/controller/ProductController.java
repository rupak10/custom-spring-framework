package com.rupak.controller;

import com.rupak.annotation.Autowired;
import com.rupak.annotation.Component;
import com.rupak.models.Product;
import com.rupak.models.dto.AddProductRequest;
import com.rupak.models.dto.AddProductResponse;
import com.rupak.service.ProductService;

@Component
public class ProductController {

    @Autowired
    private ProductService productService;

    public AddProductResponse addProduct(AddProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());

        String id = productService.addProduct(product);

        AddProductResponse addProductResponse = new AddProductResponse();
        addProductResponse.setId(id);

        return addProductResponse;
    }

    public Product getProduct(String id) {
        return productService.getProduct(id);
    }
}
