package edu.ted.servlethandler.utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class URLUtilsTest {

    @Test
    void splitWebDirToPaths() throws MalformedURLException {
        URL[] urls = URLUtils.splitWebDirToPaths("resources");
        assertEquals(4, urls.length);
        assertEquals("/resources/", urls[0].getPath());
        assertEquals("/resources/WEB-INF/", urls[1].getPath());
        assertEquals("/resources/WEB-INF/lib/", urls[2].getPath());
        assertEquals("/resources/WEB-INF/classes/", urls[3].getPath());
    }
}