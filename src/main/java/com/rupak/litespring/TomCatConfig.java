package com.rupak.litespring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupak.annotation.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class TomCatConfig {
    private static Tomcat tomcat;
    private static Context context;
    private static final String contextPath = "" ;

    public TomCatConfig(int port) {
        initTomcat(port);
    }

    private void initTomcat(int port) {
        try {
            tomcat = new Tomcat();
            tomcat.setPort(port);
            tomcat.getConnector();

            // Create a host
            Host host = tomcat.getHost();
            host.setName("localhost");
            host.setAppBase("webapps");

            tomcat.start();

            String docBase = new File(".").getAbsolutePath();
            context = tomcat.addContext(contextPath, docBase);
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    protected void registerServlet(Object instance, String className, String urlMapping) {
        tomcat.addServlet(contextPath, className, (HttpServlet) instance);
        context.addServletMappingDecoded(urlMapping, className);
    }

   /* protected static void registerServlet(Class<?> clazz, String urlMapping) throws Exception {
        if (clazz.isAnnotationPresent(RestController.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ResponseBody.class)) {
                    // Register a controller's method as a servlet for URL mapping
                    Tomcat tomcat = new Tomcat();
                    tomcat.addServlet("", clazz.getSimpleName(), new HttpServlet() {
                        @Override
                        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                            try {
                                handleControllerPostRequest(req, resp, clazz, method);
                            } catch (Exception e) {
                                throw new IOException(e);
                            }
                        }
                    });
                    tomcat.addContext("", null).addServletMappingDecoded(urlMapping, clazz.getSimpleName());
                }
            }
        }
    }*/

    protected void registerController(Class<?> clazz, Object controllerObject) throws Exception {
        if (clazz.isAnnotationPresent(RestController.class)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ResponseBody.class)) {
                    // Register a controller's method as a servlet for URL mapping
                    //Tomcat tomcat = new Tomcat();
                    String servletName = clazz.getSimpleName() + method.getName();
                    if(method.isAnnotationPresent(PostMapping.class)) {
                        String urlMapping = method.getAnnotation(PostMapping.class).value();
                        tomcat.addServlet(contextPath, servletName, new HttpServlet() {
                            @Override
                            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                                try {
                                    handleControllerPostRequest(req, resp, clazz, method, controllerObject);
                                } catch (Exception e) {
                                    throw new IOException(e);
                                }
                            }
                        });
                        context.addServletMappingDecoded(urlMapping, servletName);
                    }
                    else if(method.isAnnotationPresent(GetMapping.class)) {
                        System.out.println("get mapping found : "+method.getName());
                        String urlMapping = method.getAnnotation(GetMapping.class).value();
                        System.out.println("urlMapping : "+urlMapping);
                        tomcat.addServlet(contextPath, servletName, new HttpServlet() {
                            @Override
                            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                                try {
                                    handleControllerGetRequest(req, resp, clazz, method, urlMapping, controllerObject);
                                } catch (Exception e) {
                                    throw new IOException(e);
                                }
                            }
                        });
                        context.addServletMappingDecoded(urlMapping, servletName);
                    }
                }
            }
        }
    }

    private static void handleControllerPostRequest(HttpServletRequest req, HttpServletResponse resp, Class<?> clazz, Method method, Object controllerObject) throws Exception {
        // TODO
       // Object controller = null;


        // Extract @RequestBody parameter
        Object requestBody = null;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                // Convert JSON body to Java object using a library like Jackson
                requestBody = new ObjectMapper().readValue(req.getReader(), parameters[i].getType());
            }
        }

        // Call the method with @RequestBody parameter
        Object returnValue = method.invoke(controllerObject, requestBody);

        // Convert return object to JSON and write to the response if @ResponseBody is present
        if (method.isAnnotationPresent(ResponseBody.class)) {
            String jsonResponse = new ObjectMapper().writeValueAsString(returnValue);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);
        }
    }




    private static void handleControllerGetRequest(
            HttpServletRequest req, HttpServletResponse resp, Class<?> clazz,
            Method method, String urlMapping, Object controllerObject) throws Exception {
        // TODO
        //Object controller = null;


        // Extract path variables and request params
        Object[] args = resolveMethodArguments(req, method, urlMapping);

        // Call the method
        Object returnValue = method.invoke(controllerObject, args);

        // Convert the return object to JSON and send the response if annotated with @ResponseBody
        if (method.isAnnotationPresent(ResponseBody.class)) {
            String jsonResponse = new ObjectMapper().writeValueAsString(returnValue);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);
        }
    }

    private static Object[] resolveMethodArguments(
            HttpServletRequest req, Method method, String urlMapping) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        String path = req.getRequestURI().replace(req.getContextPath(), "");
        System.out.println("path : "+path);
        String[] urlParts = urlMapping.split("/");
        String[] pathParts = path.split("/");

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.isAnnotationPresent(PathVariable.class)) {
                // Extract path variable
                PathVariable pathVar = parameter.getAnnotation(PathVariable.class);
                String varName = pathVar.value();
                args[i] = extractPathVariable(varName, urlParts, pathParts);
            }

            if (parameter.isAnnotationPresent(RequestParam.class)) {
                // Extract request param
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                args[i] = req.getParameter(paramName);
            }
        }
        System.out.println("Arrays.toString(args) = " + Arrays.toString(args));
        return args;
    }

  /*  private static String extractPathVariable(String varName, String[] urlParts, String[] pathParts) {
        // Logic to map the path variable from the URL pattern to the actual path
        for (int j = 0; j < urlParts.length; j++) {
            if (urlParts[j].equals("{" + varName + "}")) {
                return pathParts[j];
            }
        }
        return null;
    }*/

    private static String extractPathVariable(String varName, String[] urlParts, String[] pathParts) {
        return pathParts[pathParts.length - 1];
    }
}
