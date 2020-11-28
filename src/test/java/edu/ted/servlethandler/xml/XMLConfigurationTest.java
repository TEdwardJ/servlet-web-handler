package edu.ted.servlethandler.xml;

import edu.ted.servlethandler.*;
import edu.ted.servlethandler.entity.ServletDefinition;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLConfigurationTest {

    @Test
    void givenSimpleWebXML_whenParsed_thenCorrect() throws XMLConfigurationCreationException, MalformedURLException, FileNotFoundException {
        URLClassLoader clazzLoader;
        String baseDir = "resources";

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());

        XMLConfiguration XMLConf = new XMLConfiguration("web.xml", clazzLoader);
        XMLConf.init();
        XMLConf.parse();
        Map<String, ServletDefinition> servletDefinitions = XMLConf.getServletDefinitions();
        assertTrue(servletDefinitions.containsKey("Calculator"));
        //assertTrue(servletDefinitions.containsKey("api"));
        assertEquals(0, servletDefinitions.get("Calculator").getParameters().size());
        assertEquals(1, servletDefinitions.get("Calculator").getMapping().size());
    }

    @Test
    void givenWebXML_whenParsed_thenCorrect() throws FileNotFoundException, MalformedURLException, XMLConfigurationCreationException {
        URLClassLoader clazzLoader;
        String baseDir = "resources";

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());

        XMLConfiguration XMLConf = new XMLConfiguration("webProduct.xml", clazzLoader);
        XMLConf.init();
        XMLConf.parse();
        Map<String, ServletDefinition> servletDefinitions = XMLConf.getServletDefinitions();
        assertTrue(servletDefinitions.containsKey("ProductDispatcher"));
        assertTrue(servletDefinitions.containsKey("api"));
        assertEquals(1, servletDefinitions.get("api").getParameters().size());
        assertEquals(1, servletDefinitions.get("api").getMapping().size());
        assertEquals("/WEB-INF/RestProductDispatcher-servlet.xml", servletDefinitions.get("api").getParameters().get("contextConfigLocation"));
    }

    @Test
    void givenNoWebXML_whenParsed_thenCorrect() throws MalformedURLException {
        URLClassLoader clazzLoader;

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());

        XMLConfiguration XMLConf = new XMLConfiguration("webNonExisting.xml", clazzLoader);
        assertThrows(FileNotFoundException.class, XMLConf::init);
    }

    @Test
    void givenBadWebXML_whenParsed_thenCorrect() throws MalformedURLException, FileNotFoundException {
        URLClassLoader clazzLoader;

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());

        XMLConfiguration XMLConf = new XMLConfiguration("webBad.xml", clazzLoader);
        XMLConf.init();
        assertThrows(XMLConfigurationCreationException.class, () -> XMLConf.parse());
    }
}