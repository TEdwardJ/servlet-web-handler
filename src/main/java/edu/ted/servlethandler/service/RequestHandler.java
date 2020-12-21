package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.interfaces.Handler;
import edu.ted.servlethandler.io.SimpleServletOutputStream;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;

@Slf4j
public class RequestHandler {

    private Handler handlers;

    public RequestHandler(Handler handlers) {
        this.handlers = handlers;
    }

    public void handle(Socket clientSocket) {
        try (BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream socketOutputStream = clientSocket.getOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socketOutputStream);
             SimpleServletOutputStream servletOutput = new SimpleServletOutputStream(bufferedOutputStream)) {
                SimpleHttpServletRequest request = prepareRequest(socketReader);
                SimpleHttpServletResponse response = prepareResponse(servletOutput, 0);
                handlers.handle(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SimpleHttpServletRequest prepareRequest(BufferedReader clientSocket) {
        SimpleHttpServletRequest request = getRequestFromSocket(clientSocket);
        return request;
    }

    private SimpleHttpServletResponse prepareResponse(ServletOutputStream outputStream, int status) {
        SimpleHttpServletResponse response = new SimpleHttpServletResponse(outputStream);
        response.setStatus(status);
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        return response;

    }

    SimpleHttpServletRequest getRequestFromSocket(BufferedReader socketReader) {
        SimpleHttpServletRequest request = RequestParser.parseRequestString(socketReader);
        return request;
    }

}
