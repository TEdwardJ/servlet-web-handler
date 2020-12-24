package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    @Test
    void setUrlParts_whenRequestUriContextPathAndServletPathAreSet_thenCorrect() {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String firstLine =  "POST /bbbbbb/favicon.ico HTTP/1.1";
        String hostLine = "Host: 127.0.0.1:8000";
        RequestParser.enrichRequestWithUrlAndMethod(request, firstLine, hostLine);//setUrlParts(request, url);
        assertEquals(8000, request.getLocalPort());
        assertEquals("127.0.0.1", request.getLocalAddr());
        assertEquals("/favicon.ico", request.getServletPath());
        assertEquals("/bbbbbb", request.getContextPath());
        assertEquals("/bbbbbb/favicon.ico", request.getRequestURI());
    }

    @Test
    void setUrlParts_whenRequestUriNoContextPathAndServletPathAreSet_thenCorrect() {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String firstLine =  "POST /favicon.ico HTTP/1.1";
        String hostLine = "Host: 127.0.0.1:8000";
        RequestParser.enrichRequestWithUrlAndMethod(request, firstLine, hostLine);
        assertEquals(8000, request.getLocalPort());
        assertEquals("127.0.0.1", request.getLocalAddr());
        assertEquals("/favicon.ico", request.getServletPath());
        assertNull(request.getContextPath());
        assertEquals("/favicon.ico", request.getRequestURI());
    }

    @Test
    void setUrlParts_whenRequestUriWithQueryString_thenCorrect() {
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String firstLine =  "POST /app/calculator?a=1&b=2 HTTP/1.1";
        String hostLine = "Host: 127.0.0.1:8000";
        RequestParser.enrichRequestWithUrlAndMethod(request, firstLine, hostLine);
        assertEquals(8000, request.getLocalPort());
        assertEquals("127.0.0.1", request.getLocalAddr());
        assertEquals("/app", request.getContextPath());
        assertEquals("/calculator", request.getServletPath());
        assertEquals("/app/calculator?a=1&b=2", request.getRequestURI());
        assertEquals("a=1&b=2", request.getQueryString());
    }
}