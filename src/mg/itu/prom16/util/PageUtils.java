package mg.itu.prom16.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class PageUtils {
    private final HttpServletRequest request;
    private Map<String, String> values = new HashMap<>();
    private Map<String, String> errors = new HashMap<>();

    public PageUtils(HttpServletRequest request) {
        this.request = request;
        if(request.getAttribute("errors") != null) {
            errors = ((Map<String, String>) request.getAttribute("errors"));
        }
        if(request.getAttribute("values") != null) {
            values = ((Map<String, String>) request.getAttribute("values"));
        }
    }

    public String inputFor(String name, String value) {
        String html = "";
        html += " name=\"" + name + "\"";
        html += " value=\"";
        if(!request.getParameter(name).trim().isBlank()) {
            html += request.getParameter(name);
        }
        else {
            html += value;
        }
        html += "\"";
        return html;
    }

    public String errorFor(String name) {
        if(errors.get(name) != null) {
            return "<p style=\"color: red\"> " + errors.get(name) + "</p>";
        }
        return "";
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
