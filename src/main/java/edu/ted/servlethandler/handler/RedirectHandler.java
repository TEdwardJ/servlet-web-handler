package edu.ted.servlethandler.handler;

import edu.ted.servlethandler.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectHandler extends Handler {
    @Override
    public void handleMethod(HttpServletRequest req, HttpServletResponse resp) {
        int respStatusCode = resp.getStatus();
        String redirectLocation = resp.getHeader("Location");
        if (HttpServletResponse.SC_MOVED_PERMANENTLY == respStatusCode){
            resp.reset();
            resp.setStatus(respStatusCode);
            resp.setHeader("Location", redirectLocation);
        }
    }
}
