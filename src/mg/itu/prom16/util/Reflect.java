package mg.itu.prom16.util;

import jakarta.servlet.ServletException;
import mg.itu.prom16.annotations.Controller;
import mg.itu.prom16.annotations.Get;
import mg.itu.prom16.annotations.RestApi;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reflect {
    protected static List<Class<?>> getControllers(String packageName)
            throws ServletException{

        URL url; File pkg;
        List<Class<?>> classes = new ArrayList<>();

        try {
            url = Thread.currentThread().getContextClassLoader().getResource(packageName);
            pkg = new File(url.getFile().replaceAll("\\.", "/"));
        }catch (NullPointerException e) {
            throw new ServletException("Package Controlleur non trouvé!");
        }

        if (!pkg.isDirectory()) throw new ServletException("package du controller invalide: " + packageName);

        for (File file : pkg.listFiles()) {
            if (!file.getName().endsWith(".class")) continue;

            String className = packageName + "." + file.getName().split("\\.")[0];
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) classes.add(clazz);
            } catch (ClassNotFoundException ignored) {
            }
        }

        if(classes.isEmpty()) throw new ServletException("Aucun controlleur trouvé dans: " + packageName);

        return classes;
    }

    public static Map<String, Mapping> getAllUrlMapping(String packageName, String appName)
            throws ServletException {
        Map<String, Mapping> urlMapping = new HashMap<>();
        List<Class<?>> controllers = getControllers(packageName);

        for(Class<?> controller : controllers) {
            Method[] methods = controller.getDeclaredMethods();
            for(Method method : methods) {
                if(!method.isAnnotationPresent(Get.class))  continue;

                String url = appName + method.getAnnotation(Get.class).url();

                if(urlMapping.containsKey(url))
                    throw new ServletException("Doublons pour l'url: " + url);

                Mapping mapping = new Mapping(controller.getName(), method.getName());

                mapping.isApi(method.isAnnotationPresent(RestApi.class));

                urlMapping.put(url, mapping);
            }
        }
        return urlMapping;
    }

    public static Object setObjectField(Object obj, Field field, Object value)
            throws Exception {
        Method[] methods = obj.getClass().getDeclaredMethods();
        return setObjectField(obj, methods, field, value);
    }

    public static Object setObjectField(Object obj, Method[] methods, Field field, Object value)
            throws Exception {
        String setterMethod = "set" + Utility.capitalize(field.getName());

        for(Method method : methods) {
            if(!method.getName().equals(setterMethod)) continue;

            return method.invoke(obj, value);
        }

        throw new Exception("Aucun setter trouvé pour l'attribut: " + field.getName());
    }
}
