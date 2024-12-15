package mg.itu.prom16.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationExceptionList extends Exception{
    List<ValidationException> validationExceptions = new ArrayList<>();

    public ValidationExceptionList() {

    }

    public ValidationExceptionList(List<ValidationException> validationExceptions) {
        this.validationExceptions = validationExceptions;
    }

    public void addValidationException(ValidationException validationException) {
        this.validationExceptions.add(validationException);
    }

    @Override
    public String getMessage() {
        return String.join("; ", validationExceptions.stream().map(Throwable::getMessage).toList());
    }


}


