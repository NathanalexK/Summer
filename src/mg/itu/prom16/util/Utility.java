package mg.itu.prom16.util;

public class Utility {
    public static boolean isPrimitiveType(Class<?> type) {
        switch (type.getSimpleName().toLowerCase()){
            case "int", "integer", "double", "string", "localdate", "localtime", "localdatetime" -> {
                return true;
            }
        }
        return false;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
