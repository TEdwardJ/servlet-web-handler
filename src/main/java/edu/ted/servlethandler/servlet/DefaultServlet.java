package edu.ted.servlethandler.servlet;

import edu.ted.servlethandler.service.ResourceReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLClassLoader;

public class DefaultServlet extends HttpServlet {

    private final ResourceReader resourceReader;

    public DefaultServlet(ClassLoader classLoader) {
        resourceReader = new ResourceReader(classLoader);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = resourceReader.readResource(req.getServletPath(), resp.getOutputStream());
    }
}
