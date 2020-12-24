package edu.ted.servlethandler.service;

import edu.ted.servlethandler.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ResourceReader {

    private final ClassLoader classLoader;

    public ResourceReader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String readResource(String requestURI, ServletOutputStream outputStream) {
        String resolvedPath = null;
        for (String possiblePath : MappingResolver.getAllPossiblePaths(requestURI)) {
            if (possiblePath.indexOf("/", 0) > -1) {
                resolvedPath = possiblePath.substring(1);
            } else{
                resolvedPath = possiblePath;
            }
            if (classLoader.getResource(resolvedPath) != null) {
                break;
            }
        }
        if (resolvedPath == null) {
            return null;
        }
        byte[] buffer = new byte[8192];
        int size;
        try (InputStream in = classLoader.getResourceAsStream(resolvedPath)) {
            while ((size = in.read(buffer)) > -1) {
                outputStream.write(buffer, 0, size);
            }
        } catch (IOException e) {
            log.error("Some internal error during resource reading", e);
            throw new ServerException("Some internal error during resource reading", e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resolvedPath;
    }
}
