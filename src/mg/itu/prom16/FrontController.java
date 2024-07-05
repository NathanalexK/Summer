package mg.itu.prom16;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static mg.itu.prom16.util.Reflect.*;


public class FrontController extends HttpServlet {
    protected static List<String> controllersList = null;
    protected static Map<String, Mapping> urlMapping = null;

    @Override
    public void init() throws ServletException{
        super.init();
        String packageName = this.getInitParameter("controller-package");
        urlMapping = getAllUrlMapping(packageName, getServletContext().getContextPath());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @SuppressWarnings("all")
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        String url = request.getRequestURI();
        Mapping mapping = getMapping(url);

        if(mapping == null) {
            response.sendError (HttpServletResponse.SC_NOT_FOUND, "Il n\'y a pas de methode associé a ce chemin: " + url);
            return;
        }

        try {
            Object execMethod = mapping.execMethod(request);
            if(execMethod instanceof ModelView mv){
                mv.getAttributes().forEach((key, value) -> {
                    request.setAttribute(key, value);
                });
                request.getServletContext().getRequestDispatcher(mv.getUrl())
                        .forward(request, response);

            } else if (execMethod instanceof String str){
                out.println(str);

            } else {
                throw new ServletException("Type de retour du methode: '" + mapping.getMethodName() +"' invalide");
            }
        } catch (Exception e){
            throw new ServletException(e.getMessage());
        }
    }



    protected static Mapping getMapping(String url) {
//        if(url.endsWith("/")) url = url.substring(0, url.length() - 1);
        return urlMapping.get(url);
    }
}
