package edu.ted.servlethandler.utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class URLUtilsTest {

    @Test
    void splitWebDirToPaths() throws MalformedURLException {
        URL[] urls = URLUtils.splitWebDirToPaths("resources");
        assertEquals(3, urls.length);
        assertEquals("/resources/", urls[0].getPath());
        assertEquals("/resources/WEB-INF/lib/", urls[1].getPath());
        assertEquals("/resources/WEB-INF/classes/", urls[2].getPath());
    }
}