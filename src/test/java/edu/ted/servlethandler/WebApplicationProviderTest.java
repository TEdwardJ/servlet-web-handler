package edu.ted.servlethandler;

import edu.ted.servlethandler.exception.ServletCreationException;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
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
        provider = new WebApplicationProvider(manager, new File("resources"));
    }

    @Test
    void processConfiguration() throws ServletCreationException, XMLConfigurationCreationException {
        URLClassLoader appClassLoader = provider.getClassLoader(new File("resources"));
        Map<String, ServletDefinition> servletDefinitions = new HashMap<>();
        prepareServletDefinitions(servletDefinitions);
        WebApplication webApplication = new WebApplication("resources");
        webApplication.setClassLoader(appClassLoader);
        provider.processConfiguration(servletDefinitions, webApplication);
        assertEquals("resources", webApplication.getContextPath());
        assertEquals(appClassLoader, webApplication.getClassLoader());
        //webApplication.handle();
    }

    private void prepareServletDefinitions(Map<String, ServletDefinition> servletDefinitions) {
        ServletDefinition servletDefinition0 = new ServletDefinition();
        servletDefinition0.setAlias("calculator");
        servletDefinition0.setClassIdentifier("edu.ted.servlethandler.EmptyServlet");
        servletDefinition0.addMapping("/calculator");
        servletDefinitions.put("calculator", servletDefinition0);
    }

    @Test
    void loadAndCreateClass() throws ServletCreationException, XMLConfigurationCreationException {
        URLClassLoader appClassLoader = provider.getClassLoader(new File("resources"));
        HttpServlet dummyServlet = provider.loadAndCreateClass(appClassLoader, "edu.ted.servlethandler.EmptyServlet");
        assertTrue(dummyServlet instanceof EmptyServlet);
    }

    @Test
    void getClassLoader() throws XMLConfigurationCreationException {
        final URLClassLoader classLoader = provider.getClassLoader(new File("resources"));
        assertTrue(classLoader != null);
        assertEquals(4, classLoader.getURLs().length);
    }

    @Test
    void getUniqueAppIdentifier() {
        final String uniqueAppIdentifier = provider.getUniqueAppIdentifier(new File("resources/web-calculator-1.0-SNAPSHOT.old.war"));
        assertEquals("web-calculator-1.0-SNAPSHOT.old.war-web-calculator-1.0-SNAPSHOT.dir", uniqueAppIdentifier);
    }

    @Test
    void validate() {
        assertFalse(provider.validate(new File("resources/web.xml")));
        assertFalse(provider.validate(new File("resources")));
        assertTrue(provider.validate(new File("resources/web-calculator-1.0-SNAPSHOT.old.war")));
    }
}