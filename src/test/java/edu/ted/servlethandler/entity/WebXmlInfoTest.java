package edu.ted.servlethandler.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebXmlInfoTest {
    private WebXmlInfo webXmlInfo;
    private ServletInfo servletInfo0;

    @BeforeEach
    public void init(){
        Map<String, ServletInfo> servletsInfoMap= new HashMap<>();
        webXmlInfo = new WebXmlInfo(servletsInfoMap);
        servletInfo0 = new ServletInfo();
        servletInfo0.setServletClassName("edu.ted.testpackage.MyClass");
        servletInfo0.setAlias("servlet0");

        webXmlInfo.addServletInfo(servletInfo0);

        ServletInfo servletInfo1 = new ServletInfo();
        servletInfo1.setServletClassName("edu.ted.testpackage.MyClass");
        servletInfo1.setAlias("servlet1");
        webXmlInfo.addServletInfo(servletInfo1);
    }

    @Test
    void addServletInfo() {
        assertEquals(servletInfo0, webXmlInfo.getByServletAlias("servlet0"));
        assertNotNull(webXmlInfo.getByServletAlias("servlet1"));
    }

    @Test
    void getAllServletsInfo() {
        assertEquals(2, webXmlInfo.getAllServletsInfo().keySet().size());
    }
}