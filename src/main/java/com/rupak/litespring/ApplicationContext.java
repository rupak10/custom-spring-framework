package com.rupak.litespring;

import com.rupak.annotation.Autowired;
import com.rupak.annotation.Component;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private final Map<String, Object> beanFactory = new HashMap<>();

    private static ApplicationContext applicationContext;

    private ApplicationContext(){

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createBeans(List<Class<?>> classList) throws Exception {
        for(Class<?> clazz : classList) {
            //System.out.println("class name : "+clazz);
            if(clazz.isAnnotationPresent(Component.class)) {
                Object object = clazz.getDeclaredConstructor().newInstance();
                beanFactory.put(clazz.getSimpleName(), object);
            }
        }
    }

    private void injectDependencies(List<Class<?>> classList) throws IllegalAccessException {
        for(Class<?> clazz : classList) {
            if(clazz.isAnnotationPresent(Component.class)) {
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
