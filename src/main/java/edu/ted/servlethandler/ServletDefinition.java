package edu.ted.servlethandler;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServletDefinition {
    @Getter
    @Setter
    private String alias;
    @Getter
    @Setter
    private String classIdentifier;
    @Getter
    private final Map<String, String> parameters = new HashMap<>();

    @Getter
    private final Set<String> mapping = new HashSet<>();

    public void setParameters(Map<String, String> parameters){
        this.parameters.putAll(parameters);
    }

    public void addMapping(String mappingPattern){
        mapping.add(mappingPattern);
    }

    public void addParameter(String name, String value){
        parameters.put(name, value);
    }
    public String getParameter(String name){
        return parameters.get(name);
    }


}
