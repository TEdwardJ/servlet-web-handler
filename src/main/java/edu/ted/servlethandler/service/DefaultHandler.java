package edu.ted.servlethandler.service;

import edu.ted.servlethandler.interfaces.Handler;
import edu.ted.servlethandler.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultHandler extends Handler {

    private ClassLoader permanentClassLoader;

    public DefaultHandler() {
    }

    public DefaultHandler(ClassLoader permanentClassLoader) {
        this.permanentClassLoader = permanentClassLoader;
    }

    public DefaultHandler(Handler nextHandler) {
        super(nextHandler);
    }

    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ClassLoader classLoader = (ClassLoader)req.getAttribute((String)"classLoader");
        if (classLoader == null && permanentClassLoader == null) {
            return;
        }
        HttpServlet defaultServlet = new DefaultServlet(classLoader);
        defaultServlet.service(req, resp);
        if (resp.getStatus() == 0) {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
