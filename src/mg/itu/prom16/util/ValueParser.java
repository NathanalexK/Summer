package mg.itu.prom16.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ValueParser {

    public static Object parseStringValue(String value, Class<?> type) {
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
}
