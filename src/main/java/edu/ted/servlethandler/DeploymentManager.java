package edu.ted.servlethandler;

import lombok.Getter;

import java.io.File;

public class DeploymentManager {

    @Getter
    private final ServletHandler handlers;
    private WebApplicationProvider appProvider;
    private final File destDir;

    public DeploymentManager(ServletHandler handlers, File destDir) {
        this.handlers = handlers;
        this.destDir = destDir;
    }

    protected void init(){
        appProvider = new WebApplicationProvider(this, destDir);
        appProvider.init();
    }
    protected void start(){
        appProvider.start();
    }


    void promote(WebApplication application){
        handlers.addApp(application);
    }

    public void stop() {
        appProvider.stop();
    }
}
