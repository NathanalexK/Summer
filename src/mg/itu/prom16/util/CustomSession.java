package mg.itu.prom16.util;

import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSession {
    private Map<String, Object> values;

    public CustomSession() {
        values = new HashMap<>();
    }

    public CustomSession(HttpSession httpSession) {
        this();
        this.fromHttpSession(httpSession);
    }

    public void add(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public void update(String key, Object value) {
        values.put(key, value);
    }

    public void delete(String key) {
        values.remove(key);
    }

    public void fromHttpSession(HttpSession httpSession) {
        Enumeration<String> attsEnum = httpSession.getAttributeNames();

        while (attsEnum.hasMoreElements()) {
            String attName = attsEnum.nextElement();
            this.add(attName, httpSession.getAttribute(attName));
        }
    }

    public void toHttpSession(HttpSession httpSession) {
        Enumeration<String> attsEnum = httpSession.getAttributeNames();

        while (attsEnum.hasMoreElements()) {
            String attName = attsEnum.nextElement();
            httpSession.removeAttribute(attName);
        }

        this.values.forEach((k, v) -> {
            httpSession.setAttribute(k, v);
        });
    }
}
