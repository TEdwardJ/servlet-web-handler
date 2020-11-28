package edu.ted.servlethandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServletHandler {

    private Map<String, WebApplication> appMapping = new ConcurrentHashMap<>();

    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplication application = findApplicationByPath(request.getRequestURI());
        if (application != null) {
            application.handle(request, response);
        }
    }

    private WebApplication findApplicationByPath(String path) {
        String firstPartOfPath = path.substring(1, path.indexOf("/", 1));
        return appMapping.get(firstPartOfPath);
    }



    protected void addApp(WebApplication application) {
        appMapping.put(application.getContextPath(), application);
    }
}
