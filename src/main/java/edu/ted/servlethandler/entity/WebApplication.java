package edu.ted.servlethandler.entity;

import edu.ted.servlethandler.service.MappingResolver;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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

    @Setter
    private HttpServlet defaultServlet;

    public WebApplication(String contextPath) {
        this.contextPath = contextPath;
    }

    public void addServlet(ServletMapping servletMapping) {
        mappingSet.add(servletMapping);
    }


    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean handled = false;
        String servletPath = request.getServletPath();
        for (ServletMapping servletMapping : mappingSet) {
            if (MappingResolver.resolve(servletMapping.getMapping().replace("*", ""),servletPath)!=null) {
                handled = true;
                servletMapping.getServlet().service(request, response);
                return;

            }
        }
        if (!handled) {
            defaultServlet.service(request, response);
        }
    }

}
