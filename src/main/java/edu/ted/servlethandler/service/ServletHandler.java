package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.interfaces.Handler;
import edu.ted.servlethandler.io.ResponseWriter;
import edu.ted.servlethandler.servlet.DefaultServlet;
import edu.ted.servlethandler.servlet.ExceptionServlet;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ServletHandler extends Handler {

    @Setter
    private DefaultServlet defaultServlet = new DefaultServlet(this.getClass().getClassLoader());

    private Map<String, WebApplication> appMapping = new ConcurrentHashMap<>();



    public void handleMethod(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("applicationMap", appMapping);
    }

    private WebApplication findApplicationByPath(String path) {
        if (Objects.isNull(path)) {
            return null;
        }
        return appMapping.get(path.substring(1));
    }

    @Override
    public void postActivity(HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseWriter.write((SimpleHttpServletResponse) response);
            response.flushBuffer();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void addApp(WebApplication application) {
        appMapping.put(application.getContextPath(), application);
    }
}
