package edu.ted.servlethandler;

import edu.ted.servlethandler.entity.ServletHandler;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.entity.WebApplicationProvider;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.interfaces.ShouldBeInitialized;
import lombok.Getter;

public class DeploymentManager implements CanBeStarted, ShouldBeInitialized {

    @Getter
    private final ServletHandler handlers;
    private WebApplicationProvider appProvider;

    public DeploymentManager() {
        this.handlers = new ServletHandler();
    }

    public void init(){
        appProvider = new WebApplicationProvider(this);
        appProvider.init();
    }

    public void start(){
        appProvider.start();
    }

    public void register(WebApplication application){
        handlers.addApp(application);
    }

    public void stop() {
        appProvider.stop();
    }
}
