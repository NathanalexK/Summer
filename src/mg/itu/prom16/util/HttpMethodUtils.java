package mg.itu.prom16.util;

import mg.itu.prom16.annotations.Post;
import mg.itu.prom16.enumerations.HttpMethod;

import java.lang.reflect.Method;

public class HttpMethodUtils {

    public static HttpMethod getHttpMethod(Method method) {
        if(method.isAnnotationPresent(Post.class)) return HttpMethod.POST;
        return HttpMethod.GET;
    }
}
