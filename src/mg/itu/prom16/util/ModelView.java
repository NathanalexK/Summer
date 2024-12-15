package mg.itu.prom16.util;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap<String, Object> data = new HashMap<>();
    private String redirectErrorUrl = null;


    public ModelView() {
    }

    public void redirectOnError(String url) {
        this.redirectErrorUrl = url;
    }

    public String getRedirectErrorUrl() {
        return redirectErrorUrl;
    }

    public void setRedirectErrorUrl(String redirectErrorUrl) {
        this.redirectErrorUrl = redirectErrorUrl;
    }

    public ModelView(String url) {
        this.setUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void addObject(String key, Object value) {
        this.data.put(key, value);
    }

    public HashMap<String, Object> getAttributes(){
        return this.data;
    }



}
