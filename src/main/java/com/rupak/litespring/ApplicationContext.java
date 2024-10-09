package com.rupak.litespring;

import com.rupak.annotation.Autowired;
import com.rupak.annotation.Component;
import com.rupak.annotation.RestController;
import com.rupak.annotation.Servlet;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, Object> beanFactory = new HashMap<>();
    private final int TOMCAT_PORT = 8080;
    private TomCatConfig tomCatConfig;

    private static ApplicationContext applicationContext;

    private ApplicationContext(){
        tomCatConfig = new TomCatConfig(TOMCAT_PORT);
    }

    public static synchronized ApplicationContext getInstance() {
        if(applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        return applicationContext;
    }

    protected void createSpringContainer (List<Class<?>> classList) {
        try {
            createBeans(classList);
            injectDependencies(classList);

            //initialize tomcat and bind servlets to tomcat
            //implement @RestController, @RequestBody, @ResponseBody, @PathParam, @QueryParam

            registerServlets(classList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * create all the objects based on the annotation
     */
    private void createBeans(List<Class<?>> classList) throws Exception {
        for(Class<?> clazz : classList) {
            //System.out.println("class name : "+clazz);
            if(clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Servlet.class) || clazz.isAnnotationPresent(RestController.class)) {
                Object object = clazz.getDeclaredConstructor().newInstance();
                beanFactory.put(clazz.getSimpleName(), object);
            }
        }
    }

    protected void registerServlets(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(Servlet.class)) {
                Servlet servlet = clazz.getAnnotation(Servlet.class);
                Object instance = beanFactory.get(clazz.getSimpleName());
                if(clazz.isAnnotationPresent(Servlet.class)) {
                    tomCatConfig.registerServlet(instance, clazz.getSimpleName(), servlet.urlMapping());
                }
            }
            else if(clazz.isAnnotationPresent(RestController.class)) {
                tomCatConfig.registerController(clazz, beanFactory.get(clazz.getSimpleName()));
            }
        }
    }

    /**
     * resolve object dependencies for each object
     */
    private void injectDependencies(List<Class<?>> classList) throws IllegalAccessException {
        for(Class<?> clazz : classList) {
            if(clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Servlet.class) || clazz.isAnnotationPresent(RestController.class)) {
                Object parentObject = beanFactory.get(clazz.getSimpleName());
                Field[] fields = clazz.getDeclaredFields();
                for(Field field : fields) {
                    if(field.isAnnotationPresent(Autowired.class)) {
                        Object fieldObject = beanFactory.get(field.getType().getSimpleName());
                        field.setAccessible(true);
                        field.set(parentObject, fieldObject);
                    }
                }
            }
        }
    }

    public Object getBean (Class<?> clazz) {
        return beanFactory.get(clazz.getSimpleName());
    }

    public Object getBean (String name) {
        return beanFactory.get(name);
    }

}
