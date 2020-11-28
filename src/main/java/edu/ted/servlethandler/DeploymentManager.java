package edu.ted.servlethandler;

import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.interfaces.ShouldBeInitialized;
import lombok.Getter;

import java.io.File;

public class DeploymentManager implements CanBeStarted, ShouldBeInitialized {

    @Getter
    private final ServletHandler handlers;
    private WebApplicationProvider appProvider;
    private final File destDir;

    public DeploymentManager(ServletHandler handlers, File destDir) {
        this.handlers = handlers;
        this.destDir = destDir;
    }

    public void init(){
        appProvider = new WebApplicationProvider(this, destDir);
        appProvider.init();
    }
    public void start(){
        appProvider.start();
    }


    void promote(WebApplication application){
        handlers.addApp(application);
    }

    public void stop() {
        appProvider.stop();
    }
}
