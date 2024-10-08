package com.rupak.service;

import com.rupak.annotation.Autowired;
import com.rupak.annotation.Component;
import com.rupak.models.Product;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchService {

    @Autowired
    private ProductService productService;

    public List<Product> search(String name) {
        List<Product> filterProducts = new ArrayList<>();

        List<Product> products = productService.getAllProducts();
        for(Product product : products) {
            if(product.getName().toLowerCase().contains(name)) filterProducts.add(product);
        }
        return filterProducts;
    }
}
