package edu.ted.servlethandler.handler;

import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.handler.Handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ApplicationHandler extends Handler {

    public ApplicationHandler() {
        super();
    }

    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplication application = findApplicationByPath(req);
        if (application != null) {
            application.handle(req, resp);
            req.setAttribute("classLoader", application.getClassLoader());
        }
    }


    private WebApplication findApplicationByPath(HttpServletRequest req) {
        String path = req.getContextPath();
        Map<String, WebApplication> appMapping = (Map<String, WebApplication>)req.getAttribute("applicationMap");
        if (Objects.isNull(path) || appMapping == null) {
            return null;
        }
        return appMapping.get(path.substring(1));
    }
}
