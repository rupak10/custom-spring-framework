package com.rupak.app;

import com.rupak.annotations.Autowired;
import com.rupak.annotations.Component;
import com.rupak.annotations.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> productServiceClass = Class.forName("com.rupak.service.ProductService");
        Class<?> productControllerClass = Class.forName("com.rupak.controller.ProductController");
//            String className = productServiceClass.getName();
//            System.out.println("className = " + className);

        if(productServiceClass.isAnnotationPresent(Component.class)) {
            Object productService = productServiceClass.getDeclaredConstructor().newInstance();

            /*String productId = (String) productServiceClass.getMethod("addProduct", String.class).invoke(productService, "sansung");
            System.out.println("productId = " + productId);

            String productName = (String) productServiceClass.getMethod("getProduct", String.class).invoke(productService, productId);
            System.out.println("productName = " + productName);*/
        }

        if(productControllerClass.isAnnotationPresent(RestController.class)) {
            Object productControllerObject = productControllerClass.getDeclaredConstructor().newInstance();

            Field[] fields = productControllerClass.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Autowired.class)) {
                    System.out.println(field.getName());
                }
            }

            System.out.println("Method : "+ Arrays.toString(productControllerClass.getDeclaredMethods()));





        }



    }
}
