package edu.ted.servlethandler.entity;

import edu.ted.servlethandler.entity.adapter.SimpleHttpServletRequestAdapter;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class SimpleHttpServletRequest extends SimpleHttpServletRequestAdapter {

    private String requestURI;
    private String method;
    private Map<String, String[]> parametersMap = new HashMap<>();
    private Map<String, Object[]> attributesMap = new HashMap<>();
    private Map<String, String> headers = new TreeMap<>();
    private String contextPath;
    private String servletPath;
    private InputStream input;
    private BufferedReader reader;


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
        return Optional.ofNullable(parametersMap.get(name)).filter(t -> t != null).map(t -> t[0]).get();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parametersMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return (String[]) parametersMap
                .values()
                .stream()
                .flatMap(t -> Arrays.stream(t))
                .toArray();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.parametersMap;
    }


    public void setRequestURI(String uri) {
        this.requestURI = uri;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParameter(String parameterName, String parameterValue) {
        String[] parameterValues = parametersMap.get(parameterName);
        if (parameterValues == null) {
            parametersMap.put(parameterName, new String[]{parameterValue});
        } else {
            String[] newParameterValues = (String[]) Stream.concat(Arrays.stream(parameterValues), Arrays.stream(new String[]{parameterValue})).toArray();
            parametersMap.replace(parameterName, newParameterValues);
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {

            if(Objects.isNull(reader)){
                reader = new BufferedReader(new InputStreamReader(this.input));
            }
            return reader;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.valueOf(headers.get(name));
    }

    @Override
    public Object getAttribute(String name) {
        return Optional.ofNullable(attributesMap.get(name)).filter(t -> t != null).map(t -> t[0]).orElse(null);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributesMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object o) {
        Object[] attributeValues = attributesMap.get(name);
        if (attributeValues == null) {
            attributesMap.put(name, new Object[]{o});
        } else {
            Object[] newAttributeValues = (Object[]) Stream.concat(Arrays.stream(attributeValues), Arrays.stream(new Object[]{o})).toArray();
            attributesMap.replace(name, newAttributeValues);
        }
    }

    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }

    public void setInputStream(InputStream input) {
        this.input = input;
    }
}
