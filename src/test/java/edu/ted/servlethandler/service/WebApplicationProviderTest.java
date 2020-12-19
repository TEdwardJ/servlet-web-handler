package edu.ted.servlethandler.service;

import edu.ted.servlethandler.DeploymentManager;
import edu.ted.servlethandler.EmptyServlet;
import edu.ted.servlethandler.entity.ServletInfo;
import edu.ted.servlethandler.entity.WebApplication;
import edu.ted.servlethandler.entity.WebXmlInfo;
import edu.ted.servlethandler.exception.ServletCreationException;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
import edu.ted.servlethandler.service.WebApplicationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebApplicationProviderTest {

    @Mock
    private DeploymentManager manager;

    private WebApplicationProvider provider;

    @BeforeEach
    public void init(){
        provider = new WebApplicationProvider(manager);
    }

    @Test
    void processConfiguration() throws ServletCreationException, XMLConfigurationCreationException {
        URLClassLoader appClassLoader = provider.getClassLoader(new File("resources"));
        Map<String, ServletInfo> servletDefinitions = prepareServletDefinitions();
        WebXmlInfo webXmlInfo = new WebXmlInfo(servletDefinitions);
        WebApplication webApplication = new WebApplication("resources");
        webApplication.setClassLoader(appClassLoader);
        provider.processConfiguration(webXmlInfo, webApplication);
        assertEquals("resources", webApplication.getContextPath());
        assertEquals(appClassLoader, webApplication.getClassLoader());
    }

    private Map<String, ServletInfo> prepareServletDefinitions() {
        Map<String, ServletInfo> servletDefinitions = new HashMap<>();
        ServletInfo servletInfo0 = new ServletInfo();
        servletInfo0.setAlias("calculator");
        servletInfo0.setServletClassName("edu.ted.servlethandler.EmptyServlet");
        servletInfo0.addMapping("/calculator");
        servletDefinitions.put("calculator", servletInfo0);
        return servletDefinitions;
    }

    @Test
    void loadAndCreateClass() throws ServletCreationException, XMLConfigurationCreationException {
        URLClassLoader appClassLoader = provider.getClassLoader(new File("resources"));
        HttpServlet dummyServlet = provider.loadAndCreateClass(appClassLoader, "edu.ted.servlethandler.EmptyServlet");
        assertTrue(dummyServlet instanceof EmptyServlet);
    }

    @Test
    void getClassLoader() throws XMLConfigurationCreationException {
        URLClassLoader classLoader = provider.getClassLoader(new File("resources"));
        assertTrue(classLoader != null);
        assertEquals(3, classLoader.getURLs().length);
    }

    @Test
    void getUniqueAppIdentifier() {
        String uniqueAppIdentifier = provider.getUniqueAppIdentifier(new File("WebCalculator/target/web-calculator-1.0-SNAPSHOT.war"));
        assertEquals("web-calculator-1.0-SNAPSHOT.war-web-calculator-1.0-SNAPSHOT.dir", uniqueAppIdentifier);
    }

    @Test
    void validate() {
        assertFalse(provider.validate(new File("resources/web.xml")));
        assertFalse(provider.validate(new File("resources")));
        assertTrue(provider.validate(new File("resources/web-calculator-1.0-SNAPSHOT.war")));
    }
}