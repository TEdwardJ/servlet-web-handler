package edu.ted.servlethandler.entity;

import edu.ted.servlethandler.entity.adapter.SimpleHttpServletRequestAdapter;

import java.util.*;

public class SimpleHttpServletRequest extends SimpleHttpServletRequestAdapter {

    private String requestURI;
    private String method;
    private Map<String, String[]> parametersMap = new HashMap<>();



    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override
    public String getParameter(String name) {
        return Optional.ofNullable(parametersMap.get(name)).filter(t->t != null).map(t->t[0]).get();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.parametersMap;
    }


    public void setRequestURI(String uri){
        this.requestURI = uri;
    }

    public void setMethod(String method){
        this.method = method;
    }
}
