package edu.ted.servlethandler.interfaces;


import edu.ted.servlethandler.exception.ServerException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Handler {

    Handler nextHandler;

    public Handler() {
    }

    public Handler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (resp.getStatus() == 0 || req.getAttribute("EXCEPTION") != null) {
            try {
                handleMethod(req, resp);
            } catch (Exception e) {
                req.setAttribute("EXCEPTION", new ServerException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            }
        }
        if (resp.getStatus() == 0 || req.getAttribute("EXCEPTION") != null) {
            if (nextHandler != null) {

                    nextHandler.handle(req, resp);
            }
            postActivity(req, resp);
        }
    }

    public abstract void handleMethod(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException;

    public Handler thenHandle(Handler handler) {
        this.nextHandler = handler;
        return this.nextHandler;
    }

    public void postActivity(HttpServletRequest request, HttpServletResponse response){

    }

    public void preActivity(HttpServletRequest request, HttpServletResponse response){

    }

}
