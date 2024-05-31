package mg.itu.prom16.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Mapping {
    private String className;
    private String methodName;

    private Mapping(){}

    public Mapping(String className, String methodName){
        this.setClassName(className);
        this.setMethodName(methodName);
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
}
