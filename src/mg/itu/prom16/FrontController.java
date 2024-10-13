package mg.itu.prom16;


import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.http.HttpException;
import mg.itu.prom16.page.PageError;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.MyJSON;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static mg.itu.prom16.util.Reflect.*;


public class FrontController extends HttpServlet {
    protected static List<String> controllersList = null;
    protected static Map<String, Mapping> urlMapping = null;
    protected static boolean firstInit = true;

    @Override
    public void init() throws ServletException{
        super.init();


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


        try {
            if(firstInit) {
                String packageName = this.getInitParameter("controller-package");
                urlMapping = getAllUrlMapping(packageName, getServletContext().getContextPath());
                firstInit = false;
            }

            PrintWriter out = response.getWriter();
            String url = request.getRequestURI();
            Mapping mapping = getMapping(url);

            if(mapping == null) {
                throw new HttpException (HttpServletResponse.SC_NOT_FOUND, "Il n\'y a pas de methode associé a ce chemin: " + url);
            }
//            Object execMethod = mapping.execMapping(request, response);
            mapping.execMapping(request, response);
//            if(mapping.isApi()) {
//                response.setContentType("application/json");
//                Gson gson = new MyJSON().getGson();
//                out.write(gson.toJson(execMethod));
//                return;
//            }
//
//            if(execMethod instanceof ModelView mv){
//                mv.getAttributes().forEach((key, value) -> {
//                    request.setAttribute(key, value);
//                });
//                request.getServletContext().getRequestDispatcher(mv.getUrl())
//                        .forward(request, response);
//
//            } else if (execMethod instanceof String str){
//                out.println(str);
//
//            } else {
//                throw new ServletException("Type de retour du methode: '" + mapping.getMethodName() +"' invalide");
//            }
        } catch (HttpException httpException) {
            PageError.showPage(response, httpException.getHttpStatus(), httpException.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }



    protected static Mapping getMapping(String url) {
//        if(url.endsWith("/")) url = url.substring(0, url.length() - 1);
        return urlMapping.get(url);
    }
}
