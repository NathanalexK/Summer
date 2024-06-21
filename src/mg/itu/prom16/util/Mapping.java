package mg.itu.prom16.util;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.annotations.Param;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public Method[] getMethods()
            throws Exception{
        Object object = Class.forName(className).getDeclaredConstructor().newInstance();
        return object.getClass().getDeclaredMethods();
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
            if(!method.getName().equalsIgnoreCase(methodName)) continue;

            return method.invoke(object);
        }

        throw new Exception("Method Not Found: " + methodName + " in class: " + className);
    }

    public Object execMethod(HttpServletRequest request)
            throws Exception {
        Object object = Class.forName(className).getDeclaredConstructor().newInstance();

        for (Method method : getMethods()){
            if(!method.getName().equalsIgnoreCase(methodName)) continue;

            Paranamer paranamer = new AdaptiveParanamer();
            Parameter[] parameters = method.getParameters();
            String[] paramNames = paranamer.lookupParameterNames(method);
            Object[] paramValues = new Object[parameters.length];

            for(int i = 0; i < parameters.length; i++){
                String paramName = null;

                if(parameters[i].isAnnotationPresent(Param.class))  paramName = parameters[i].getAnnotation(Param.class).name();
                else                                                paramName = paramNames[i];

                System.out.println(paramName);
                paramValues[i] = getValue(request, paramName, parameters[i].getType());
            }

            return method.invoke(object, paramValues);
        }
        throw new Exception("Method Not Found: " + methodName + " in class: " + className);
    }

    public Object parseValue(String value, Class<?> type){
        String paramType = type.getSimpleName().toLowerCase();
        switch (paramType) {
            case "localdate" -> {
                return LocalDate.parse(value);
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
        if (Utility.isPrimitiveType(parmType))
            return parseValue(request.getParameter(paramName), parmType);


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
