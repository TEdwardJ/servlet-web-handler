package edu.ted.servlethandler.entity;

import java.util.Map;

public class WebXmlInfo {

    private final Map<String, ServletInfo> servletDefinitions;

    public WebXmlInfo(Map<String, ServletInfo> servletDefinitions) {
        this.servletDefinitions = servletDefinitions;
    }

    public void addServletInfo(ServletInfo servletInfo){
        servletDefinitions.put(servletInfo.getAlias(), servletInfo);
    }
    public ServletInfo getByServletAlias(String alias){
        return servletDefinitions.get(alias);
    }

    public Map<String, ServletInfo> getAllServletsInfo(){
        return servletDefinitions;
    }
}
