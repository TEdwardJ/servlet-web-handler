package edu.ted.servlethandler.service;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceReader {

    private final ClassLoader classLoader;

    public ResourceReader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean readResource(String requestURI, ServletOutputStream outputStream) {
        String resolvedPath = null;
        for (String possiblePath : MappingResolver.getAllPossiblePaths(requestURI)) {
            if (possiblePath.indexOf("/", 0) > -1) {
                resolvedPath = possiblePath.substring(1);
            }
            if (classLoader.getResource(resolvedPath) != null) {
                break;
            }
        }
        if (resolvedPath == null) {
            return false;
        }
        byte[] buffer = new byte[8192];
        int size;
        try (InputStream in = classLoader.getResourceAsStream(resolvedPath)) {
            while ((size = in.read(buffer)) > -1) {
                outputStream.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
