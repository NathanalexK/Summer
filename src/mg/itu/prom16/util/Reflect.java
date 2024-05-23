package mg.itu.prom16.util;

import mg.itu.prom16.annotations.Controller;
import mg.itu.prom16.annotations.Get;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reflect {
    protected static List<Class<?>> getControllers(String packageName){
        List<Class<?>> classes = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
        File pkg = new File(url.getFile().replaceAll("\\.", "/"));

        if(pkg.isDirectory()) {
            for(File file : pkg.listFiles()){
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().split("\\.")[0];
                    try {
                        Class<?> clazz = Class.forName(className);
                        if(clazz.isAnnotationPresent(Controller.class)) classes.add(clazz);
                    }catch (ClassNotFoundException ignored){
                    }
                }
            }
        }

        return classes;
    }

    public static Map<String, Mapping> getAllUrlMapping(String packageName, String appName){
        Map<String, Mapping> urlMapping = new HashMap<>();

        List<Class<?>> controllers = getControllers(packageName);

        for(Class<?> controller : controllers){
            Method[] methods = controller.getDeclaredMethods();

            for(Method method : methods){
                if(method.isAnnotationPresent(Get.class)) {
                    String url = appName + method.getAnnotation(Get.class).url();
                    Mapping mapping = new Mapping(controller.getName(), method.getName());

                    urlMapping.put(url, mapping);
                }
            }
        }

        return urlMapping;
    }
}
