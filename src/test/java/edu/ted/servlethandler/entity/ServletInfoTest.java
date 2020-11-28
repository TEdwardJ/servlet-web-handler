package edu.ted.servlethandler.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ServletInfoTest {

    @Test
    void getAlias() {
        ServletInfo servletDefinition = new ServletInfo();
        servletDefinition.setAlias("calculator");
        assertEquals("calculator", servletDefinition.getAlias());
    }

    @Test
    void getServletClassName() {
        ServletInfo servletDefinition = new ServletInfo();
        servletDefinition.setServletClassName("edu.ted.testpackage.MyClass");
        assertEquals("edu.ted.testpackage.MyClass", servletDefinition.getServletClassName());
    }

    @Test
    void getParameters() {
        ServletInfo servletDefinition = new ServletInfo();
        servletDefinition.addParameter("param1", "value1");
        servletDefinition.addParameter("param2", "value2");
        final Map<String, String> servletParameters = servletDefinition.getParameters();
        assertEquals(2, servletParameters.size());
        assertEquals("value1", servletParameters.get("param1"));
        assertEquals("value2", servletParameters.get("param2"));
    }

    @Test
    void getMapping() {
        ServletInfo servletDefinition = new ServletInfo();
        servletDefinition.addMapping("/map1");
        servletDefinition.addMapping("/map2");
        final Set<String> mappingSet = servletDefinition.getMapping();
        assertEquals(2, mappingSet.size());
        assertTrue(mappingSet.contains("/map1"));
        assertTrue(mappingSet.contains("/map2"));

    }
}