package mg.itu.prom16.util;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotations.Param;
import mg.itu.prom16.annotations.Post;
import mg.itu.prom16.enumerations.HttpMethod;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Mapping {
    private String className;
    private String methodName;
    private Boolean isApi = false;
    private HttpMethod httpMethod = HttpMethod.GET;
//    private Object instance;
//    private String[] parametersName;

    private Mapping() {
    }

    public Mapping(String className, String methodName) throws ServletException {
        this.setClassName(className);
        this.setMethodName(methodName);
//        if()
    }

    public Mapping(Class<?> controller, Method method) {
        this.setClassName(controller.getName());
        this.setMethodName(method.getName());
        if(method.isAnnotationPresent(Post.class)) this.setHttpMethod(HttpMethod.POST);
    }

    public Mapping(String className, String methodName, HttpMethod method) throws ServletException {
        this.setClassName(className);
        this.setMethodName(methodName);
        this.setHttpMethod(method);
    }

    public Boolean isApi() {
        return isApi;
    }

    public void isApi(Boolean api) {
        isApi = api;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method[] getMethods()
        throws Exception {
//        Object object = Class.forName(className).getDeclaredConstructor().newInstance();
        return Class.forName(className).getDeclaredMethods();
    }

    public Method getMethod(String methodName) throws Exception {
        for (Method method : getMethods()) {
            if (!method.getName().equals(methodName)) continue;

            return method;
        }
        throw new Exception("Method Not Found: " + methodName + " in class: " + className);

    }

//    public Object execMethod()
//            throws Exception {
//
//
//        for (Method method : object.getClass().getDeclaredMethods()){
//            if(!method.getName().equalsIgnoreCase(methodName)) continue;
//
//            return method.invoke(object);
//        }
//
//        throw new Exception("Method Not Found: " + methodName + " in class: " + className);
//    }

    public Object execMethod(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        CustomSession customSession = null;

        if(!request.getMethod().equalsIgnoreCase(this.getHttpMethod().name())){
            throw new ServletException("Mehod [" + this.getHttpMethod().name().toUpperCase() +"] not allowed");
        }

        Constructor<?> constructor = Class.forName(className).getConstructors()[0];
        Parameter[] constructorParams = constructor.getParameters();
        Object[] constructorArgs = new Object[constructorParams.length];

        for (int i = 0; i < constructorParams.length; i++) {
            if (constructorParams[i].getType() == CustomSession.class) {
                customSession = new CustomSession(request.getSession());
                constructorArgs[i] = customSession;
            }
        }

        Object controller = null;
        controller = constructor.newInstance(constructorArgs);

        Method method = getMethod(methodName);

        Paranamer paranamer = new AdaptiveParanamer();
        Parameter[] parameters = method.getParameters();
        String[] paramNames = paranamer.lookupParameterNames(method);
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName = null;

            if (parameters[i].isAnnotationPresent(Param.class)) paramName = parameters[i].getAnnotation(Param.class).name();

            else if (parameters[i].getType().equals(CustomSession.class)) {
                if (customSession == null) customSession = new CustomSession(request.getSession());
                paramValues[i] = customSession;
                continue;
            } else
                throw new ServletException("etu2498: Annotation @Param de la methode:" + this.getMethodName() + " introuvable");

            paramValues[i] = getValue(request, paramName, parameters[i].getType());
        }

        Object invoked = method.invoke(controller, paramValues);
        if (customSession != null) customSession.toHttpSession(request.getSession());
        return invoked;

    }

    public Object parseValue(String value, Class<?> type) {
        String paramType = type.getSimpleName().toLowerCase();

        System.out.println(value);
        switch (paramType) {
            case "localdate" -> {
                return LocalDate.parse(value, DateTimeUtils.DATE_FORMATTER);
            }
            case "localtime" -> {
                return LocalTime.parse(value);
            }
            case "localdatetime" -> {
                return LocalDateTime.parse(value);
            }
            case "int", "integer" -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException | NullPointerException e) {
                    return 0;
                }
            }
            case "double" -> {
                try {
                    return Double.parseDouble(value);
                } catch (NullPointerException | NumberFormatException e) {
                    return 0;
                }
            }
            case "string" -> {
                return value;
            }
            default -> {
                return type.cast(value);
            }
        }
    }

    public Object getValue(HttpServletRequest request, String paramName, Class<?> parmType)
        throws Exception {
        System.out.println(paramName + ": " + request.getParameter(paramName));
        if (Utility.isPrimitiveType(parmType)) {
            return parseValue(request.getParameter(paramName), parmType);
        }

        Object obj = parmType.getDeclaredConstructor().newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        Method[] methods = obj.getClass().getDeclaredMethods();

        for (Field field : fields) {
            Object value = parseValue(request.getParameter(paramName + "." + field.getName()), field.getType());
            Reflect.setObjectField(obj, methods, field, value);
        }
        return obj;
    }

}
