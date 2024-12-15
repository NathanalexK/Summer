package mg.itu.prom16.util;

import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mg.itu.prom16.annotations.Param;
import mg.itu.prom16.annotations.RestApi;
import mg.itu.prom16.enumerations.HttpMethod;
import mg.itu.prom16.exception.FormException;
import mg.itu.prom16.exception.ValidationException;
import mg.itu.prom16.exception.ValidationExceptionList;
import mg.itu.prom16.http.HttpException;
import mg.itu.prom16.page.ContentType;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpMethodAction {
    private HttpMethod httpMethod;
    private String action;
    private String actionClass;
    private Boolean isApi;

    public HttpMethodAction(HttpMethod httpMethod, Method action, Class<?> actionClass) {
        this.setHttpMethod(httpMethod);
        this.setAction(action);
        this.setActionClass(actionClass);
        this.setApi(action.isAnnotationPresent(RestApi.class));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Class<?> getActionClass() throws Exception {
        return Class.forName(this.actionClass);
    }

    public void setActionClass(Class<?> actionClass) {
        this.actionClass = actionClass.getName();
    }

    public Boolean getApi() {
        return isApi;
    }

    public void setApi(Boolean api) {
        isApi = api;
    }

    public Method getAction() throws Exception {
        Class<?> clazz = this.getActionClass();
        for(Method method: clazz.getDeclaredMethods()) {
            if(method.getName().equalsIgnoreCase(this.action)){
                return method;
            }
        }
        throw new Exception("No method found: " + clazz.getName() + "." + this.action + "()");
//        return action;
    }

    public void setAction(Method action) {
        this.action = action.getName();
    }

    public void execMethod(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        CustomSession customSession = null;

        Class<?> controllerClass = this.getActionClass();

        if(!request.getMethod().equalsIgnoreCase(this.getHttpMethod().name())){
            throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mehod [" + this.getHttpMethod().name().toUpperCase() +"] not allowed");
        }

        Constructor<?> constructor = controllerClass.getConstructors()[0];
        Parameter[] constructorParams = constructor.getParameters();
        Object[] constructorArgs = new Object[constructorParams.length];

        for (int i = 0; i < constructorParams.length; i++) {
            if (constructorParams[i].getType() == CustomSession.class) {
                customSession = new CustomSession(request.getSession());
                constructorArgs[i] = customSession;
            }
        }

//        request.setAttribute();

        Object controller;
        controller = constructor.newInstance(constructorArgs);

        Method actionMethod = getAction();

        Paranamer paranamer = new AdaptiveParanamer();
        Parameter[] parameters = actionMethod.getParameters();
        String[] paramNames = paranamer.lookupParameterNames(actionMethod);
        Object[] paramValues = new Object[parameters.length];

        Map<String, String> error = new HashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            String paramName = null;

            if (parameters[i].isAnnotationPresent(Param.class)) paramName = parameters[i].getAnnotation(Param.class).name();

            else if (parameters[i].getType().equals(CustomSession.class)) {
                if (customSession == null) customSession = new CustomSession(request.getSession());
                paramValues[i] = customSession;
                continue;
            } else
                throw new ServletException("etu2498: Annotation @Param de la methode:" + actionMethod.getName() + " introuvable");

//            try {
            paramValues[i] = getValueFromRequest(request, paramName, parameters[i].getType());
//            } catch (ValidationException ve) {
//                error.put()
//            }
        }

        Object invoked = actionMethod.invoke(controller, paramValues);
        if (customSession != null) customSession.toHttpSession(request.getSession());

        PrintWriter out = response.getWriter();
        if(this.getApi()) {
            response.setContentType(ContentType.JSON);
            Gson gson = new MyJSON().getGson();
            out.write(gson.toJson(invoked));
            out.close();
            return;
        }

        if(invoked instanceof ModelView mv){
            processModelView(request, response, mv);
//            mv.getAttributes().forEach(request::setAttribute);
//            request.getServletContext().getRequestDispatcher(mv.getUrl())
//                .forward(request, response);

        } else if (invoked instanceof String str){
            out.println(str);

        } else {
            throw new ServletException("Type de retour du methode: '" + actionMethod.getName() +"' invalide");
        }
        out.close();
    }

    public void processModelView(HttpServletRequest request, HttpServletResponse response, ModelView mv) throws ServletException, IOException {
        mv.getAttributes().forEach(request::setAttribute);
        Object hasErrorObj = request.getAttribute("hasError");
        System.out.println("hasErrorObj: " + hasErrorObj);
        if(hasErrorObj != null && hasErrorObj instanceof Boolean hasError && hasError) {
            System.out.println("Has error = true");
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request){
                @Override
                public String getMethod() {
                    return "GET";
                }
            };
            System.out.println("New Method: " + request.getMethod());
            String referer = "/" + request.getHeader("Referer");
            request.setAttribute("hasError", null);
            request.getServletContext().getRequestDispatcher(mv.getRedirectErrorUrl()).forward(wrapper, response);
            return;
        }


        request.getServletContext().getRequestDispatcher(mv.getUrl())
            .forward(request, response);
    }

    public Object getValueFromRequest(HttpServletRequest request, String paramName, Class<?> parmType)
            throws Exception {
        request.setAttribute("hasError", false);
        System.out.println(paramName + ": " + request.getParameter(paramName));
        if (Utility.isPrimitiveType(parmType)) {
            return  ValueParser.parseStringValue(request.getParameter(paramName), parmType);
        }

        if(parmType == CustomFile.class){
            Part part = request.getPart(paramName);
            if(part == null) return null;
            return new CustomFile(part);
        }

        Object obj = parmType.getDeclaredConstructor().newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        Method[] methods = obj.getClass().getDeclaredMethods();

        Map<String, Object> model = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        boolean hasError = false;
        for (Field field : fields) {
            Object value = null;
            String requestAttName = paramName + "." + field.getName();



            if(field.getType() == CustomFile.class) {
                Part part = request.getPart(requestAttName);
                if(part == null) continue;
                value = new CustomFile(part);
            } else if(field.getType() == byte[].class) {
                Part part = request.getPart(requestAttName);
                if(part == null) continue;
                value = part.getInputStream().readAllBytes();
            }

            else {
                value = ValueParser.parseStringValue(request.getParameter(requestAttName), field.getType());
            }

            try {
                model.put(requestAttName, value);
                Reflect.setObjectField(obj, methods, field, value);
            } catch (ValidationException ve) {
                hasError = true;
                errorMap.put(requestAttName, ve.getError());
            }
        }
        if(hasError) {
            System.out.println("iiioooo");
            request.setAttribute("hasError", true);
            request.setAttribute("errors", errorMap);
//            request.setAttribute("model", model);
//            System.out.println("errorMapppp eeee");
//            throw new FormException(errorMap);
//            String error = "";
//            throw new Exception("Exception found in Form:");
        }

        return obj;
    }

    @Override
    public String toString() {
        return httpMethod.name() + " - " + isApi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, action);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof HttpMethodAction hma)) {
            return false;
        }

        return this.action.equals(hma.action) && this.httpMethod.equals(hma.httpMethod);
    }
}
