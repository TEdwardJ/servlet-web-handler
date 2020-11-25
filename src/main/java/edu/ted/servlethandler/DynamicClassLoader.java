package edu.ted.servlethandler;

import lombok.Getter;

import java.net.URL;
import java.net.URLClassLoader;


public class DynamicClassLoader extends URLClassLoader {

    @Getter
    private final URL[] classPath;

    public DynamicClassLoader(URL[] classPath, ClassLoader parent) {
        super(classPath, parent);
        this.classPath = classPath;
    }
}
