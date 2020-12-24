package edu.ted.servlethandler;

import edu.ted.servlethandler.service.ServletHandler;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.service.WebApplicationProvider;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import lombok.Getter;

public class DeploymentManager implements CanBeStarted {

    @Getter
    private final ServletHandler handlers;
    private WebApplicationProvider appProvider;

    public DeploymentManager() {
        this.handlers = new ServletHandler();
    }

    public void init(){
        appProvider = new WebApplicationProvider(this);
    }

    public void start(){
        init();
        appProvider.start();
    }

    public void register(WebApplication application){
        handlers.addApp(application);
    }

    public void stop() {
        appProvider.stop();
    }
}
