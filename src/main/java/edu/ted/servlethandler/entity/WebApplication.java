package edu.ted.servlethandler.entity;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class WebApplication {
    @Getter
    private final String contextPath;
    @Getter
    @Setter
    private URLClassLoader classLoader;
    private final Set<ServletMapping> mappingSet = new HashSet<>();

    public WebApplication(String contextPath) {
        this.contextPath = contextPath;
    }

    public void addServlet(ServletMapping servletMapping) {
        mappingSet.add(servletMapping);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String applicationPartRequestURI = request.getRequestURI().replace("/" + contextPath, "");
        for (ServletMapping servletMapping : mappingSet) {
            if(servletMapping.getMapping().replace("*","").startsWith(applicationPartRequestURI)){
                servletMapping.getServlet().service(request, response);
            }
        }
    }

}
