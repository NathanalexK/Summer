package mg.itu.prom16.util;

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
}
