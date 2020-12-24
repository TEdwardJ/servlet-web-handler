package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.interfaces.Handler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
public class RequestHandler extends Handler {

    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RequestParser.parseRequestAndEnrichRequestEntity(req.getReader(), req);
        prepareResponse(resp);
    }

    private HttpServletResponse prepareResponse(HttpServletResponse resp) {
        resp.setHeader("Content-Type", "text/html; charset=utf-8");
        return resp;

    }

    @Override
    public void postActivity(HttpServletRequest request, HttpServletResponse response) {
        if(response.getStatus() == 0){
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
