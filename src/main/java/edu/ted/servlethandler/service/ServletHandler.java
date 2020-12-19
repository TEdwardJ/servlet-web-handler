package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.io.ResponseWriter;
import edu.ted.servlethandler.servlet.DefaultServlet;
import edu.ted.servlethandler.servlet.ExceptionServlet;
import lombok.Setter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ServletHandler {

    @Setter
    private ExceptionServlet exceptionServlet = new ExceptionServlet();
    private DefaultServlet defaultServlet = new DefaultServlet(this.getClass().getClassLoader());

    private Map<String, WebApplication> appMapping = new ConcurrentHashMap<>();

    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        //classLoader.getResource()
        WebApplication application = findApplicationByPath(request.getContextPath());
        if (application != null) {
            try {
                application.handle(request, response);
            } catch (Exception e) {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                request.setAttribute("EXCEPTION", e);

                ((SimpleHttpServletRequest) request).setMethod("GET");
                //response.set//sendError
                //        (HttpResponseCode.INTERNAL_ERROR.getCode(),HttpResponseCode.INTERNAL_ERROR.getReasonPhrase());
                exceptionServlet.service(request, response);
            }

        } else {
            defaultServlet.service(request, response);
        }
        ResponseWriter.write((SimpleHttpServletResponse) response);
        response.flushBuffer();
    }

    private WebApplication findApplicationByPath(String path) {
        if (Objects.isNull(path)) {
            return null;
        }
/*        String firstPartOfPath;
        if (path.indexOf("/", 1) > -1) {
            firstPartOfPath = path.substring(1, path.indexOf("/", 1));
        } else{
            firstPartOfPath = path.substring(1);
        }*/
        return appMapping.get(path.substring(1));
    }


    public void addApp(WebApplication application) {
        appMapping.put(application.getContextPath(), application);
    }
}
