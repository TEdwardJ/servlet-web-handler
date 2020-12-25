package edu.ted.servlethandler.handler;

import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.handler.Handler;
import edu.ted.servlethandler.io.ResponseWriter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServletHandler extends Handler {

    private Map<String, WebApplication> appMapping = new ConcurrentHashMap<>();

    public void handleMethod(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("applicationMap", appMapping);
    }

    @Override
    public void postActivity(HttpServletRequest request, HttpServletResponse response) {
        try {
            ResponseWriter.write((SimpleHttpServletResponse) response);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Some unexpected error, seems the request cannot be processed: ", e);
        }
    }

    public void addApp(WebApplication application) {
        appMapping.put(application.getContextPath(), application);
    }
}
