package edu.ted.servlethandler.servlet;

import edu.ted.servlethandler.service.MappingResolver;
import edu.ted.servlethandler.service.ResourceReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Objects;

public class DefaultServlet extends HttpServlet {

    private final ResourceReader resourceReader;

    public DefaultServlet(ClassLoader classLoader) {
        resourceReader = new ResourceReader(classLoader);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resource = resourceReader.readResource(req.getServletPath(), resp.getOutputStream());
        if (!Objects.isNull(resource)) {
            resp.setContentType(resolveResourceType(resource));
        }
    }

    String resolveResourceType(String path) {
        String[] pathFragments = path.split("\\.");
        String extension = pathFragments[1].toLowerCase();
        if ("html".equals(extension) || "htm".equals(extension)) {
            return "text/html; charset=utf-8";
        } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "image/jpeg";
        } else if ("bmp".equals(extension)) {
            return "image/bmp";
        } else if ("gif".equals(extension)) {
            return "image/gif";
        } else if ("ico".equals(extension)) {
            return "image/x-icon";
        } else if ("png".equals(extension)) {
            return "image/png";
        } else if ("pdf".equals(extension)) {
            return "application/pdf";
        } else if ("txt".equals(extension)) {
            return "text/plain; charset=utf-8";
        }
        return "application/octet-stream";
    }

}
