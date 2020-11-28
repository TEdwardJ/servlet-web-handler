package edu.ted.servlethandler.xml;

import edu.ted.servlethandler.*;
import edu.ted.servlethandler.entity.ServletInfo;
import edu.ted.servlethandler.exception.XMLConfigurationCreationException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLConfigurationReaderTest {

    @Test
    void givenSimpleWebXML_whenParsed_thenCorrect() throws XMLConfigurationCreationException, MalformedURLException, URISyntaxException, FileNotFoundException {
        URLClassLoader clazzLoader;

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());
        File configFile = new File(clazzLoader.getResource("web.xml").toURI());
        XMLConfigurationReader xmlConf = new XMLConfigurationReader();
        xmlConf.init();
        xmlConf.parse(configFile);
        Map<String, ServletInfo> servletDefinitions = xmlConf.getServletDefinitions();
        assertTrue(servletDefinitions.containsKey("Calculator"));
        assertEquals(0, servletDefinitions.get("Calculator").getParameters().size());
        assertEquals(1, servletDefinitions.get("Calculator").getMapping().size());
    }

    @Test
    void givenWebXML_whenParsed_thenCorrect() throws MalformedURLException, XMLConfigurationCreationException, URISyntaxException, FileNotFoundException {
        URLClassLoader clazzLoader;

        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());
        File configFile = new File(clazzLoader.getResource("webProduct.xml").toURI());
        XMLConfigurationReader xmlConf = new XMLConfigurationReader();
        xmlConf.init();
        xmlConf.parse(configFile);
        Map<String, ServletInfo> servletDefinitions = xmlConf.getServletDefinitions();
        assertTrue(servletDefinitions.containsKey("ProductDispatcher"));
        assertTrue(servletDefinitions.containsKey("api"));
        assertEquals(1, servletDefinitions.get("api").getParameters().size());
        assertEquals(1, servletDefinitions.get("api").getMapping().size());
        assertEquals("/WEB-INF/RestProductDispatcher-servlet.xml", servletDefinitions.get("api").getParameters().get("contextConfigLocation"));
    }

    @Test
    void givenNoWebXML_whenFileNotFoundException_thenCorrect() {
        File configFile = new File("webNonExisting.xml");
        XMLConfigurationReader xmlConf = new XMLConfigurationReader();
        xmlConf.init();
        assertThrows(FileNotFoundException.class, ()->xmlConf.parse(configFile));
    }

    @Test
    void givenBadWebXML_whenParsed_thenCorrect() throws MalformedURLException, URISyntaxException {
        URLClassLoader clazzLoader;
        URL[] classPathURLs = new URL[]{new URL("file:resources")};
        clazzLoader = new DynamicClassLoader(classPathURLs, getClass().getClassLoader());
        File configFile = new File(clazzLoader.getResource("webBad.xml").toURI());
        XMLConfigurationReader xmlConf = new XMLConfigurationReader();
        xmlConf.init();
        assertThrows(XMLConfigurationCreationException.class, ()->xmlConf.parse(configFile));
    }
}