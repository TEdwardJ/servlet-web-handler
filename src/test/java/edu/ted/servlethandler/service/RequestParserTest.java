package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    @Test
    void setUrlParts_whenRequestUriContextPathAndServletPathAreSet_thenCorrect() {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String url = "http://127.0.0.1:8000/bbbbbb/favicon.ico";
        RequestParser.setUrlParts(request, url);
        assertEquals("favicon.ico",request.getServletPath());
        assertEquals("/bbbbbb",request.getContextPath());
        assertEquals("/bbbbbb/favicon.ico",request.getRequestURI());
    }

    @Test
    void setUrlParts_whenRequestUriNoContextPAthAndServletPathAreSet_thenCorrect() {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String url = "http://127.0.0.1:8000/favicon.ico";
        RequestParser.setUrlParts(request, url);
        assertEquals("favicon.ico",request.getServletPath());
        assertNull(request.getContextPath());
        assertEquals("/favicon.ico",request.getRequestURI());
    }
}