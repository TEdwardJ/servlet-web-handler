package edu.ted.servlethandler.service;

import java.util.Arrays;
import java.util.List;

public class MappingResolver {

    static List<String> getAllPossiblePaths(String path) {
        if ("/".equals(path) || path.isEmpty()) {
            return Arrays.asList("/index.html", "/index.htm", "/");
        }
        return Arrays.asList(path);
    }

    public static String resolve(String mapping, String url) {
        for (String possiblePath : getAllPossiblePaths(url)) {
            if (mapping.startsWith(possiblePath) && !possiblePath.equals("/")) {
                return possiblePath;
            } else if (possiblePath.equals("/") && mapping.equals(possiblePath)) {
                return possiblePath;
            }
        }
        return null;
    }
}
