package mg.itu.prom16.util;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.lang.reflect.Method;

public class HttpMethodExecutor {


    public void executeHttpMehod(HttpServletRequest request, HttpServletResponse response, HttpMethodAction action, Object object)
            throws Exception  {
        PrintWriter out = response.getWriter();
        if(action.getApi()) {
            response.setContentType("application/json");
            Gson gson = new MyJSON().getGson();
            out.write(gson.toJson(object));
            return;
        }

        if(object instanceof ModelView mv){
            mv.getAttributes().forEach(request::setAttribute);
            request.getServletContext().getRequestDispatcher(mv.getUrl())
                .forward(request, response);

        } else if (object instanceof String str){
            out.println(str);

        } else {
            throw new ServletException("Type de retour du methode: '" + action.getAction().getName() +"' invalide");
        }
    }
}
