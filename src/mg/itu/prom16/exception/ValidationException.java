package mg.itu.prom16.exception;

import java.util.List;

public class ValidationException extends Exception{
    private String field;
    private String error;

    public ValidationException(String field, String error) {
//        super(msg);
        setField(field);
        setError(error);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
