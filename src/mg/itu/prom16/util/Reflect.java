package mg.itu.prom16.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotations.*;
import mg.itu.prom16.annotations.model.*;
import mg.itu.prom16.enumerations.HttpMethod;
import mg.itu.prom16.exception.ValidationException;
import mg.itu.prom16.exception.ValidationExceptionList;
import mg.itu.prom16.http.HttpException;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

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
            throws Exception {
        Map<String, Mapping> urlMapping = new HashMap<>();
        List<Class<?>> controllers = getControllers(packageName);

        for(Class<?> controller : controllers) {
            Method[] methods = controller.getDeclaredMethods();
            Set<HttpMethodAction> hmaSet = new HashSet<>();

            for(Method method : methods) {
                if(!method.isAnnotationPresent(Url.class)) continue;

                String url = appName + method.getAnnotation(Url.class).url();
                HttpMethod actionHttpMethod = HttpMethodUtils.getHttpMethod(method);
                HttpMethodAction methodAction = new HttpMethodAction(actionHttpMethod, method, controller);

                if(!hmaSet.add(methodAction)) {
                    throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Duplicate method name for: " + controller.getName() + "." + method.getName());
                }

                if(!urlMapping.containsKey(url)) {
                    Mapping mapping = new Mapping();
                    mapping.addHttpMethodAction(methodAction);
                    urlMapping.put(url, mapping);
                    continue;
                }

                Mapping mapping = urlMapping.get(url);
                if(mapping.containsHttpMethod(actionHttpMethod)){
                    throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Duplicate url for Http mehod: " + actionHttpMethod.name() + " url: " + url);
                }

                System.out.println(methodAction);
                mapping.addHttpMethodAction(methodAction);
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

        checkField(field, value);

        String setterMethod = "set" + Utility.capitalize(field.getName());
        for(Method method : methods) {
            if(!method.getName().equals(setterMethod)) continue;

            Object invoked = method.invoke(obj, value);
            return invoked;
        }

        throw new Exception("Aucun setter trouvé pour l'attribut: " + field.getName());
    }

    protected static boolean hasHttpMehod(Method method) {
        return method.isAnnotationPresent(Get.class) || method.isAnnotationPresent(Post.class);
    }

    protected static void checkField(Field field, Object value) throws ValidationException {
//        List<ValidationException> validationExceptions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if(field.isAnnotationPresent(Required.class) && !ValidationUtils.checkRequired(String.valueOf(value))) {
            errors.add("Field " + field.getName() + " is required");
//            validationExceptions.add(new ValidationException("Field " + field.getName() + " is required"));
        }

        if(field.isAnnotationPresent(Numeric.class) && !ValidationUtils.isNumeric(String.valueOf(value))) {
            errors.add("Field " + field.getName() + " must be numeric");
//            validationExceptions.add(new ValidationException("Field " + field.getName() + " must be numeric"));
        }

        if(field.isAnnotationPresent(Length.class) && !ValidationUtils.checkLength(String.valueOf(value), field.getAnnotation(Length.class).value())) {
            errors.add("Length of " + field.getName() + " must be below " + field.getAnnotation(Length.class).value());
//            validationExceptions.add(new ValidationException("Length of " + field.getName() + " must be below " + field.getAnnotation(Length.class).value()));
        }

        if(field.isAnnotationPresent(Range.class) && !ValidationUtils.checkRange(String.valueOf(value), field.getAnnotation(Range.class).min(), field.getAnnotation(Range.class).max())) {
            errors.add("Range of " + field.getName() + " must be between " + field.getAnnotation(Range.class).min() + " and " + field.getAnnotation(Range.class).max());
//            validationExceptions.add(new ValidationException("Range of " + field.getName() + " must be between " + field.getAnnotation(Range.class).min() + " and " + field.getAnnotation(Range.class).max()));
        }

        if(field.isAnnotationPresent(Email.class) && !ValidationUtils.checkEmail(String.valueOf(value))) {
            errors.add(field.getName() + " must be an email");
//            validationExceptions.add(new ValidationException(field.getName() + " must be an email"));
        }

        if(field.isAnnotationPresent(Regex.class) && !ValidationUtils.checkRegex(String.valueOf(value), field.getAnnotation(Regex.class).value())) {
            errors.add(field.getName() + " does not matches with pattern: \"" + field.getAnnotation(Regex.class).value() + "\"");
//            validationExceptions.add(new ValidationException(field.getName() + " does not matches with pattern: \"" + field.getAnnotation(Regex.class).value() + "\""));
        }

        if(!errors.isEmpty()) {
//            throw new ValidationExceptionList(validationExceptions);
            throw new ValidationException(field.getName(), String.join("; ", errors));
        }
    }
}
