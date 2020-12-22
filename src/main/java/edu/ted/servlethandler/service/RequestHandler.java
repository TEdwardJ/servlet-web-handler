package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.interfaces.Handler;
import edu.ted.servlethandler.io.SimpleServletOutputStream;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
public class RequestHandler extends Handler {


    public RequestHandler() {

    }

/*    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            SimpleHttpServletRequest request = getRequestFromSocket(req.getReader(), req);
            SimpleHttpServletResponse response = prepareResponse(resp.getOutputStream(), 0);
            handlers.handle(request, response);
    }*/

    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getRequestFromSocket(req.getReader(), req);
        prepareResponse(resp);
    }

    private HttpServletResponse prepareResponse(HttpServletResponse resp) {
        //resp.setStatus(status);
        resp.setHeader("Content-Type", "text/html; charset=utf-8");
        return resp;

    }

    SimpleHttpServletRequest getRequestFromSocket(BufferedReader socketReader, HttpServletRequest req) {
        SimpleHttpServletRequest request = RequestParser.parseRequestString(socketReader, req);
        return request;
    }

    @Override
    public void postActivity(HttpServletRequest request, HttpServletResponse response) {
        if(response.getStatus() == 0){
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
