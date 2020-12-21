package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.interfaces.Handler;
import edu.ted.servlethandler.servlet.ExceptionServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandler extends Handler {
    private ExceptionServlet exceptionServlet = new ExceptionServlet();

    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.reset();
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (req.getAttribute("EXCEPTION") == null || !(req.getAttribute("EXCEPTION") instanceof Exception)) {
            return;
        }

        ((SimpleHttpServletRequest) req).setMethod("GET");
        exceptionServlet.service(req, resp);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
