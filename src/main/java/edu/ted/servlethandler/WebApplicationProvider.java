package edu.ted.servlethandler;

import edu.ted.servlethandler.exception.ServletCreationError;
import edu.ted.servlethandler.exception.XMLConfigurationCreationError;
import edu.ted.servlethandler.scanner.Scanner;
import edu.ted.servlethandler.utils.URLUtils;
import edu.ted.servlethandler.xml.XMLConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class WebApplicationProvider {
    private Scanner scanner;
    private final File destDir;

    private final DeploymentManager deploymentManager;
    private final String tempDirectory;

    public WebApplicationProvider(DeploymentManager deploymentManager, File destDir) {
        this.deploymentManager = deploymentManager;
        this.destDir = destDir;
        this.tempDirectory = System.getProperty("java.io.tmpdir");
    }

    protected void init() {
        scanner = new Scanner(destDir.getPath(), WebApplicationProvider.this::fileAdded);
        scanner.setInterval(1);
    }

    protected void start() {
        scanner.start();
    }

    public void fileAdded(File file) {
        if (validate(file)) {
            try {
                File tempDestDir = WarUnwrapper.unwrap(file, tempDirectory + "/" + getUniqueAppIdentifier(file));
                if (Objects.isNull(tempDestDir)) {
                    return;
                }
                URLClassLoader appClassLoader = getClassLoader(tempDestDir);
                XMLConfiguration xmlConfiguration = getConfigurationFromXML(tempDestDir, appClassLoader);
                WebApplication newApplication = new WebApplication(file.getName().substring(0, file.getName().lastIndexOf(".")));
                newApplication.setClassLoader(appClassLoader);
                processConfiguration(xmlConfiguration.getServletDefinitions(), newApplication);
                deploymentManager.promote(newApplication);
            } catch (ServletCreationError servletCreationError) {
                log.error("Configuration has not been created", servletCreationError);
            } catch (XMLConfigurationCreationError xmlConfigurationCreationError) {
                log.error("Configuration has not been loaded", xmlConfigurationCreationError);
            }
        }
    }

    void processConfiguration(Map<String, ServletDefinition> servletDefinitions, WebApplication application) throws ServletCreationError {
        URLClassLoader appClassLoader = application.getClassLoader();
        for (ServletDefinition definition : servletDefinitions.values()) {
            String servletClassIdentifier = definition.getClassIdentifier();
            HttpServlet newInstance = loadAndCreateClass(appClassLoader, servletClassIdentifier);
            for (String mapping : definition.getMapping()) {
                application.addServlet(new ServletMapping(newInstance, mapping));
            }
        }
    }

    HttpServlet loadAndCreateClass(URLClassLoader appClassLoader, String servletClassIdentifier) throws ServletCreationError {
        Class<?> servletClass;
        try {
            servletClass = appClassLoader.loadClass(servletClassIdentifier);
            return (HttpServlet) servletClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Instance of class {} has not been created. ", servletClassIdentifier, e);
            throw new ServletCreationError(e);
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("Error when instantiation of class {}. ", servletClassIdentifier, e);
            throw new ServletCreationError(e);
        }

    }

    private XMLConfiguration getConfigurationFromXML(File tempDestDir, URLClassLoader appClassLoader) throws XMLConfigurationCreationError {
        XMLConfiguration xmlConfiguration = new XMLConfiguration("web.xml", appClassLoader);
        try {
            xmlConfiguration.init();
            xmlConfiguration.parse();
            return xmlConfiguration;
        } catch (FileNotFoundException e) {
            log.error("Configuration reading from file {} failed.", tempDestDir, e);
            throw new XMLConfigurationCreationError(e);
        }
    }

    URLClassLoader getClassLoader(File tempDestDir) throws XMLConfigurationCreationError {
        URL[] urlList;
        try {
            urlList = URLUtils.splitWebDirToPaths(tempDestDir.getPath());
            return new DynamicClassLoader(urlList, WebApplicationProvider.class.getClassLoader());
        } catch (MalformedURLException e) {
            log.error("Error while creating classLoader based on path list", e);
            throw new XMLConfigurationCreationError(e);
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

    public interface ScannerListener {
        void fileAdded(File file);
    }
}
