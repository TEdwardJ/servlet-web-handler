package edu.ted.servlethandler;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.entity.SimpleHttpServletResponse;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.io.SimpleServletOutputStream;
import edu.ted.servlethandler.service.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class WebServer implements CanBeStarted {

    private final static int DEFAULT_PORT = 8000;

    private final int port;
    private DeploymentManager manager;
    private ServletHandler handlers;
    private RequestHandler requestHandler;

    private volatile boolean toBeShutDowned = false;

    public WebServer() {
        this(DEFAULT_PORT);
    }

    public WebServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        init();
        manager.start();

        try (ServerSocket socket = new ServerSocket(port)) {
            while (!isToBeShutDowned()) {
                try {
                    Socket clientSocket = socket.accept();
                    startHandler(clientSocket);
                } catch (IOException e) {
                    log.info("The attempt to establish connection with client is failed: ", e);
                }
            }
        } catch (Exception e) {
            log.info("The SimpleWebServer is to down immediately due to some unexpected error: ", e);
        }
    }

    private void startHandler(Socket clientSocket) {
        new Thread(() -> this.handle(clientSocket)).start();
    }

    void handle(Socket clientSocket){
        try(OutputStream socketOutputStream = clientSocket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socketOutputStream);
            SimpleServletOutputStream servletOutput = new SimpleServletOutputStream(bufferedOutputStream)) {
            SimpleHttpServletRequest request = new SimpleHttpServletRequest();
            request.setInputStream(clientSocket.getInputStream());
            SimpleHttpServletResponse response = new SimpleHttpServletResponse(servletOutput);
            handlers.handle(request, response);
        } catch (Exception e) {
            log.error("The request cannot be processed due to error ", e);
        }
    }

    private void init() {
        manager = new DeploymentManager();
        handlers = manager.getHandlers();
        requestHandler = new RequestHandler();
        handlers
                .thenHandle(requestHandler)
                .thenHandle(new ApplicationHandler())
                .thenHandle(new RedirectHandler())
                .thenHandle(new DefaultHandler())
                .thenHandle(new ExceptionHandler())
                .thenHandle(new DefaultHandler(this.getClass().getClassLoader()));
    }

    @Override
    public void stop() {
        manager.stop();
    }


    public boolean isToBeShutDowned() {
        return toBeShutDowned;
    }

    public void setToBeShutDowned() {
        toBeShutDowned = true;
    }

    public static void main(String[] args) {
        new WebServer().start();
    }
}
