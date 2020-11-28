package edu.ted.servlethandler;

import edu.ted.servlethandler.entity.ServletInfo;
import edu.ted.servlethandler.entity.ServletMapping;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.entity.WebXmlInfo;
import edu.ted.servlethandler.exception.ServletCreationException;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
import edu.ted.servlethandler.interfaces.CanBeStarted;
import edu.ted.servlethandler.interfaces.ShouldBeInitialized;
import edu.ted.servlethandler.scanner.WebAppWatchingScanner;
import edu.ted.servlethandler.utils.URLUtils;
import edu.ted.servlethandler.utils.WarUnwrapper;
import edu.ted.servlethandler.xml.XMLConfigurationReader;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

@Slf4j
public class WebApplicationProvider implements CanBeStarted, ShouldBeInitialized {
    private CanBeStarted scanner;
    private final File webappsDirectory;

    private final DeploymentManager deploymentManager;
    private final String tempDirectory;
    private XMLConfigurationReader xmlConfiguration;

    public WebApplicationProvider(DeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
        this.webappsDirectory = new File("webapps");
        this.tempDirectory = System.getProperty("java.io.tmpdir");
    }

    public void init() {
        WebAppWatchingScanner scanner = new WebAppWatchingScanner(webappsDirectory, WebApplicationProvider.this::deployWebApplication);
        this.scanner = scanner;
        ((ShouldBeInitialized) scanner).init();
        xmlConfiguration = new XMLConfigurationReader();
        xmlConfiguration.init();
    }

    public void start() {
        scanner.start();
    }

    public void deployWebApplication(File file) {
        if (validate(file)) {
            try {
                File tempDestDir = WarUnwrapper.unwrap(file, tempDirectory + "/" + getUniqueAppIdentifier(file));
                if (Objects.isNull(tempDestDir)) {
                    return;
                }
                URLClassLoader appClassLoader = getClassLoader(tempDestDir);
                WebXmlInfo webXmlInfo = getConfigurationFromXML(tempDestDir, appClassLoader);
                WebApplication newApplication = new WebApplication(file.getName().substring(0, file.getName().lastIndexOf(".")));
                newApplication.setClassLoader(appClassLoader);
                processConfiguration(webXmlInfo, newApplication);
                deploymentManager.register(newApplication);
            } catch (ServletCreationException e) {
                log.error("Configuration has not been created", e);
            } catch (XMLConfigurationCreationException e) {
                log.error("Configuration has not been loaded", e);
            }
        }
    }

    void processConfiguration(WebXmlInfo webAppInfo, WebApplication application) throws ServletCreationException {
        URLClassLoader appClassLoader = application.getClassLoader();
        for (ServletInfo definition : webAppInfo.getAllServletsInfo().values()) {
            String servletClassIdentifier = definition.getServletClassName();
            for (String mapping : definition.getMapping()) {
                application.addServlet(new ServletMapping(loadAndCreateClass(appClassLoader, servletClassIdentifier), mapping));
            }
        }
    }

    HttpServlet loadAndCreateClass(URLClassLoader appClassLoader, String servletClassIdentifier) throws ServletCreationException {
        Class<?> servletClass;
        try {
            servletClass = appClassLoader.loadClass(servletClassIdentifier);
            return (HttpServlet) servletClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Instance of class {} has not been created. ", servletClassIdentifier, e);
            throw new ServletCreationException(e);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("Error when instantiation of class {}. ", servletClassIdentifier, e);
            throw new ServletCreationException(e);
        }
    }

    private WebXmlInfo getConfigurationFromXML(File tempDestDir, URLClassLoader appClassLoader) throws XMLConfigurationCreationException {
        try {
            File configFile = new File(appClassLoader.getResource("web.xml").toURI());
            if (!configFile.exists()){
                throw new FileNotFoundException();
            }
            return xmlConfiguration.parse(configFile);
        } catch (FileNotFoundException | URISyntaxException e) {
            log.error("Configuration reading from file {} failed.", tempDestDir, e);
            throw new XMLConfigurationCreationException(e);
        }
    }

    URLClassLoader getClassLoader(File tempDestDir) throws XMLConfigurationCreationException {
        URL[] urlList;
        try {
            urlList = URLUtils.splitWebDirToPaths(tempDestDir.getPath());
            return new DynamicClassLoader(urlList, WebApplicationProvider.class.getClassLoader());
        } catch (MalformedURLException e) {
            log.error("Error while creating classLoader based on path list", e);
            throw new XMLConfigurationCreationException(e);
        }
    }

    String getUniqueAppIdentifier(File file) {
        String fileName = file.getName();
        return fileName + "-" + fileName.substring(0, fileName.lastIndexOf(".")) + ".dir";
    }

    boolean validate(File file) {
        //is file?
        if (file.isDirectory()) {
            return false;
        }
        //Extension war?
        return file.getName().endsWith(".war");
    }

    public void stop() {
        scanner.stop();
    }

}
