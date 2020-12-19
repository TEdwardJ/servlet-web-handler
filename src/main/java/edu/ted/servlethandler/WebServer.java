package edu.ted.servlethandler;

import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.service.RequestHandler;
import edu.ted.servlethandler.service.RequestParser;
import edu.ted.servlethandler.service.ServletHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
        new Thread(() -> {
            requestHandler.handle(clientSocket);
        }).start();
    }


    private void init() {
        manager = new DeploymentManager();
        manager.init();
        handlers = manager.getHandlers();
        requestHandler = new RequestHandler(handlers);
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
