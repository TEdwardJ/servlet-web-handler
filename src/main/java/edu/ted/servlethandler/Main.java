package edu.ted.servlethandler;

import edu.ted.servlethandler.utils.URLUtils;

import javax.servlet.http.HttpServlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class Main {
    public static void main(String[] args) {

        URLClassLoader clazzLoader;
        String baseDir = "C:\\Users\\Ted\\AppData\\Local\\Temp\\testWebApp\\";
        try {

            URL[] classPathURLs = URLUtils.getClassPathFromBasePaths(URLUtils.splitWebDirToPaths(baseDir));

            clazzLoader = new DynamicClassLoader(classPathURLs, Main.class.getClassLoader());

            Class<?> cl = clazzLoader.loadClass("org.springframework.web.servlet.DispatcherServlet");
            Class<?> clClassesSource = clazzLoader.loadClass("com.study.dao.UserRowMapper");
            if (HttpServlet.class.isAssignableFrom(cl)) {
                System.out.println(cl.getName());
                System.out.println(clClassesSource.getName());
                    HttpServlet newInstance = (HttpServlet) cl.newInstance();
                //newInstance.init();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }/* catch (ServletException e) {
                e.printStackTrace();
            }*/ catch (InstantiationException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
