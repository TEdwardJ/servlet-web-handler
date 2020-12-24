package edu.ted.servlethandler.servlet;

import edu.ted.servlethandler.exception.ServerException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        Exception exception = (Exception) req.getAttribute("EXCEPTION");
        int errorCode = Integer.valueOf(((ServerException)exception).getResponseCode());
        out.write("<B>Error code:</B> " + errorCode);
        out.write("<br><pre>");
        exception.printStackTrace(out);
        out.write("</pre>");
        out.flush();
    }
}
