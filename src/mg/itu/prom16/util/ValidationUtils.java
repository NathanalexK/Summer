package mg.itu.prom16.util;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean checkRequired(String str) {
        System.out.println(str);
        if(str == null) return false;
        return !str.isBlank();
    }

    public static  boolean isNumeric(String str) {
        System.out.println(str);
        try {
//            D.parseInt(str);
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

//    public static boolean

    public static boolean checkLength(String str, int len) {
        System.out.println(str);
        return str.length() <= len;
    }

    public static boolean checkRange(String str, double min, double max) {
        System.out.println(str);
        try {
            double d = Double.parseDouble(str);
            return  d >= min && d <= max;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkEmail(String str) {
        return str != null && EMAIL_PATTERN.matcher(str).matches();
    }

    public static boolean checkRegex(String str, String regex) {
        return str != null && Pattern.compile(regex).matcher(str).matches();
    }
}
