package edu.ted.servlethandler.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class URLUtils {
    public static List<URL> getClassPathFromBasePath(URL baseURL) {
        List<URL> classPath = new ArrayList<>();
        File baseDir = null;
        try {
            baseDir = new File(baseURL.toURI());
        } catch (URISyntaxException e) {
            log.error("Bad URL {}", baseURL, e);
        }
        classPath.add(baseURL);
        if (baseDir.isDirectory()) {
            for (File f : baseDir.listFiles(f -> f.getName().endsWith(".jar"))) {
                File file = f;
                try {
                    classPath.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    log.error("", e);
                }
            }
        }
        return classPath;
    }

    public static URL[] getClassPathFromBasePaths(URL[] urls) {
        List<URL> urlList = Arrays.stream(urls).flatMap(url -> getClassPathFromBasePath(url).stream()).collect(Collectors.toList());
        URL[] URLArray = new URL[urlList.size()];
        for (int i = 0; i < urlList.size(); i++) {
            URLArray[i] = urlList.get(i);
        }
        return URLArray;
    }

    public static URL[] splitWebDirToPaths(String baseDir) throws MalformedURLException {
        String generalPath = "file:\\" + baseDir + "\\";
        URL baseDirURL = new URL(generalPath);
        log.debug("Base Dir URL creation: {}", generalPath);

        String classesPath = "file:\\" + baseDir + "\\WEB-INF\\classes\\";
        log.debug("Base classes Dir URL creation: {}", classesPath);
        URL baseClassesDirURL = new URL(classesPath);
        String libPath = "file:\\" + baseDir + "\\WEB-INF\\lib\\";
        log.debug("Base lib Dir URL creation: {}", libPath);
        URL baseLibDirURL = new URL(libPath);
        return new URL[]{baseDirURL, baseLibDirURL, baseClassesDirURL};
    }
}
