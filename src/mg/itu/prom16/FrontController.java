package mg.itu.prom16;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotations.Controller;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.Reflect;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static mg.itu.prom16.util.Reflect.*;


public class FrontController extends HttpServlet {
    protected static List<String> controllersList = null;
    protected static Map<String, Mapping> urlMapping = null;

    @Override
    public void init() throws ServletException {
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
            throws IOException {
        PrintWriter out = response.getWriter();
        String url = request.getRequestURI();
        Mapping mapping = getMapping(url);
        if(mapping != null) {
            try {
                Object execMethod = mapping.execMethod();

                if(execMethod instanceof ModelView mv){
                    mv.getAttributes().forEach((key, value) -> {
                        request.setAttribute(key, value);
                    });
                    request.getServletContext().getRequestDispatcher(mv.getUrl())
                            .forward(request, response);
                }
                else {
                    out.println(execMethod);
                }

//                out.println(mapping.execMethod());
            } catch (Exception e){
                e.printStackTrace(out);
            }
        } else {
            out.println("Il n\'y a pas de methode associé a ce chemin: " + url);
        }

    }



    protected static Mapping getMapping(String url) {
        if(url.endsWith("/")) url = url.substring(0, url.length() - 1);
        return urlMapping.get(url);

    }
}
