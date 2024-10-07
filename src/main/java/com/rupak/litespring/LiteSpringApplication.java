package com.rupak.litespring;

import com.rupak.annotation.PackageScan;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiteSpringApplication {

    public static ApplicationContext run(Class<?> appClass) throws ClassNotFoundException {
        PackageScan packageScan = appClass.getAnnotation(PackageScan.class);
        System.out.println("Package to scan : "+ Arrays.toString(packageScan.scanPackages()));

        //get the class loader object
        ClassLoader classLoader = LiteSpringApplication.class.getClassLoader();

        List<Class<?>> classes = new ArrayList<>();

        for(String packageName : packageScan.scanPackages()) {
            URL resource = classLoader.getResource(packageName.replace('.', '/'));
            //System.out.println("resource : "+resource.getPath());
            //System.out.println("Path : "+new File(resource.getPath()));
            if(resource!= null) {
                classes.addAll(ClassScanner.scan(new File(resource.getPath()), packageName));
            }
        }
        //System.out.println("classes : "+classes);

        ApplicationContext applicationContext = ApplicationContext.getInstance();
        applicationContext.createSpringContainer(classes);

        return applicationContext;
    }
}
