package mg.itu.prom16.util;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.annotations.Param;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class Mapping {
    private String className;
    private String methodName;
//    private String[] parametersName;

    private Mapping(){}

    public Mapping(String className, String methodName){
        this.setClassName(className);
        this.setMethodName(methodName);
//        this.setParametersName(parametersName);
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

//    public void setParametersName(String[] parametersName){
//        this.parametersName = parametersName;
//    }
//
//    public String[] getParametersName() {
//        return this.parametersName;
//    }

    public Object execMethod()
            throws Exception {
        Object object = Class.forName(className).getDeclaredConstructor().newInstance();

        for (Method method : object.getClass().getDeclaredMethods()){
            if(method.getName().equalsIgnoreCase(methodName)){
                return method.invoke(object);
            }
        }

        throw new Exception("Method Not Found: " + methodName + " in class: " + className);
    }

    public Object execMethod(HttpServletRequest request)
            throws Exception {
        Object object = Class.forName(className).getDeclaredConstructor().newInstance();

        for (Method method : object.getClass().getDeclaredMethods()){
            if(method.getName().equalsIgnoreCase(methodName)){
                Parameter[] parameters = method.getParameters();
                Object[] paramValues = new Object[parameters.length];

                for(int i = 0; i < parameters.length; i++){
                    if(parameters[i].isAnnotationPresent(Param.class)){
                        paramValues[i] = parseValue(request.getParameter(parameters[i].getAnnotation(Param.class).name()), parameters[i].getType());
                    }
                    else {
                        paramValues[i] = parseValue(request.getParameter(parameters[i].getName()), parameters[i].getType());
                        System.out.println(parameters[i].getName());
                    }


                }

                return method.invoke(object, paramValues);
            }
        }
        throw new Exception("Method Not Found: " + methodName + " in class: " + className);
    }

    public Object parseValue(String value, Class<?> type){
        String paramType = type.getSimpleName().toLowerCase();
        Object parsed;
        switch (paramType) {
            case "int":
            case "integer":
                parsed = Integer.parseInt(value);
                break;
            case "double":
                parsed = Double.parseDouble(value);
                break;
            default:
                parsed = value;
                break;
        }
        return parsed;
    }
}
