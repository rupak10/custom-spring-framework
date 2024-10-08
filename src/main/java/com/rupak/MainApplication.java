package com.rupak;

import com.rupak.annotation.PackageScan;
import com.rupak.controller.ProductController;
import com.rupak.litespring.ApplicationContext;
import com.rupak.litespring.LiteSpringApplication;
import com.rupak.models.Product;
import com.rupak.models.dto.AddProductRequest;
import com.rupak.models.dto.AddProductResponse;

@PackageScan(scanPackages = {"com.rupak"})
public class MainApplication {

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("---Custom spring application running---");

        ApplicationContext applicationContext = LiteSpringApplication.run(MainApplication.class);

       /* ProductController productController = (ProductController) applicationContext.getBean(ProductController.class);
        AddProductResponse addProductResponse = productController.addProduct(new AddProductRequest("Iphone"));
        System.out.println("addProductResponse = " + addProductResponse);

        Product product = productController.getProduct(addProductResponse.getId());
        System.out.println("product = " + product);*/
    }
}
