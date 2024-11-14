package mg.itu.prom16.exception;

import java.util.HashMap;
import java.util.Map;

public class FormException extends Exception{
    Map<String, String> errors = new HashMap<>();

    public FormException(Map<String, String> errors) {
        super("Exception found in form");
        this.errors = errors;
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder("<ul>");
        for(Map.Entry<String, String> entry: errors.entrySet()) {
            html.append("<li><b>").append(entry.getKey()).append(": </b>").append(entry.getValue()).append("</li>");
        }

        html.append("</ul>");
        return html.toString();
    }

}
