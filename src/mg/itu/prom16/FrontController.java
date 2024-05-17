package mg.itu.prom16;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotations.Controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FrontController extends HttpServlet {
    protected static boolean checked = false;
    protected static List<String> controllersList = null;

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
        out.println("Hello");

        if(!checked) {
            controllersList = new ArrayList<>();
            String packageName = this.getInitParameter("controller-package");
            out.println(packageName);
            List<Class<?>> classes = getClasses(packageName);


            for(Class clazz : classes){
                if(clazz.isAnnotationPresent(Controller.class)) controllersList.add(clazz.getName());
            }
        }

        for(String className : controllersList){
            out.println("- " + className);
        }
    }

    protected List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
        File pkg = new File(url.getFile().replaceAll("\\.", "/"));

        if(pkg.isDirectory()) {
            for(File file : pkg.listFiles()){
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().split("\\.")[0];
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    }catch (ClassNotFoundException ignored){
                    }
                }
            }
        }

        return classes;
    }
}
