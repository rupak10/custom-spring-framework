package com.rupak.controller;

import com.rupak.annotation.*;
import com.rupak.models.Product;
import com.rupak.models.dto.AddProductRequest;
import com.rupak.models.dto.AddProductResponse;
import com.rupak.models.dto.SearchResponse;
import com.rupak.service.ProductService;
import com.rupak.service.SearchService;

import java.util.List;

@Component
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SearchService searchService;

    @PostMapping("/api/products")
    @ResponseBody
    public AddProductResponse addProduct(@RequestBody AddProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());

        String id = productService.addProduct(product);

        AddProductResponse addProductResponse = new AddProductResponse();
        addProductResponse.setId(id);

        return addProductResponse;
    }

  /*  @GetMapping("/api/products/{id}")
    @ResponseBody
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }*/

    @GetMapping("/api/products/*")
    @ResponseBody
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }

   /* @GetMapping("/api/products/{id}")
    @ResponseBody
    public SearchResponse search(@RequestParam("query") String query) {
        List<Product> productList = searchService.search(query);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(productList);
        return searchResponse;
    }*/

    @GetMapping("/api/products/search")
    @ResponseBody
    public SearchResponse search(@RequestParam("query") String query) {
        System.out.println("query : "+query);
        List<Product> productList = searchService.search(query);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setProducts(productList);
        return searchResponse;
    }
}
