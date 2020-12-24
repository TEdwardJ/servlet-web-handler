package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.exception.ServerException;
import edu.ted.servlethandler.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class RequestParser {

    private static final Pattern METHOD_AND_URL_PATTERN = Pattern.compile("^(?<method>[A-Z]+) (?<resource>[^ ]+) (?<version>[^ ]+)");
    private static final Pattern URL_PARTS_PATTERN = Pattern.compile("(http://(?<host>[^:]+)(:(?<port>[0-9]{1,5}))*)*(?<requestUri>(?<contextPart>/[^/]+)*(?<servletPart>/[^?]*)([?](?<queryString>.+$))*)");

    private RequestParser() {
        throw new AssertionError("No com.study.util.RequestParser instances for you!");
    }

    public static void parseRequestAndEnrichRequestEntity(BufferedReader socketReader, HttpServletRequest req) {
        try {
            String requestString = readSocket(socketReader);
            if (requestString.isEmpty()) {
                return;
            }
            enrichRequestByRequestString(requestString, req);
        } catch (Exception e) {
            log.error("Some internal error during request processing: ", e);
            throw new ServerException("Some internal error during request preparation", e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static String readSocket(BufferedReader in) throws IOException {
        StringBuilder request = new StringBuilder();
        boolean continueReceiving = true;
        while (continueReceiving) {
            String line = in.readLine();
            if (!line.isEmpty()) {
                request.append(line).append(Constants.EOL);
            } else {
                continueReceiving = false;
            }
        }
        return request.toString();
    }

    static void parseThenSetParameters(SimpleHttpServletRequest request, String parametersString) {
        for (String part : parametersString.split("&")) {
            String[] parameterNameValuePair = part.split("=");
            request.setParameter(parameterNameValuePair[0], parameterNameValuePair[1]);
        }
    }

    static void enrichRequestByRequestString(String requestString, HttpServletRequest req) throws IOException {
        boolean headersEnd = false;
        BufferedReader socketReader = req.getReader();
        SimpleHttpServletRequest request = (SimpleHttpServletRequest) req;
        String[] requestLines = requestString.split("\n");
        for (int i = 1; i < requestLines.length; i++) {
            if (requestLines[i].startsWith("Host")) {
                enrichRequestWithUrlAndMethod(request, requestLines[0], requestLines[i]);
                //enrichRequestWithHostAndPort(request, requestLines[i]);
            } else if ("\n".equals(requestLines[i]) || "\r\n".equals(requestLines[i])) {
                headersEnd = true;
            } else if (!headersEnd) {
                enrichRequestWithHeaders(request, requestLines[i]);
            } else if (headersEnd) {
                parseThenSetParameters(request, requestLines[i]);
            }
        }
        if ("POST".equals(request.getMethod())) {
            parseThenSetParameters(request, getPostBody(socketReader, request));
        }
        return;
    }

    private static String getPostBody(BufferedReader socketReader, SimpleHttpServletRequest request) {
        int bodySize = request.getIntHeader("Content-Length");
        char[] body = new char[bodySize];
        try {
            socketReader.read(body, 0, bodySize);
            return String.copyValueOf(body);
        } catch (IOException e) {
            log.error("Some internal error during POST request processing: ", e);
            throw new ServerException("Some internal error during POST request processing: ", e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    static void enrichRequestWithHostAndPort(SimpleHttpServletRequest request, String requestLine) {
        String[] secondRequestLineParams = requestLine.split("(: |:)");
        request.setLocalAddr(secondRequestLineParams[1]);
        request.setLocalPort(Integer.parseInt(secondRequestLineParams[2]));
    }

    static void enrichRequestWithUrlAndMethod(SimpleHttpServletRequest request, String requestLine, String hostLine) {
        enrichRequestWithHostAndPort(request, hostLine);
        Matcher requestMatcher = METHOD_AND_URL_PATTERN.matcher(requestLine);
        if (requestMatcher.find()) {
            String methodText = requestMatcher.group("method");
            String url = requestMatcher.group("resource");
            String version = requestMatcher.group("version");
            setUrlParts(request, url);
            request.setMethod(methodText);
        } else {
            throw new ServerException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    static void setUrlParts(SimpleHttpServletRequest request, String url) {
        Matcher requestMatcher = URL_PARTS_PATTERN.matcher(url);
        if (requestMatcher.find()) {
            String requestUri = requestMatcher.group("requestUri");
            String contextPath = requestMatcher.group("contextPart");
            String servletPath = requestMatcher.group("servletPart");
            String queryString = requestMatcher.group("queryString");
            request.setRequestURI(requestUri);
            request.setContextPath(contextPath);
            request.setServletPath(servletPath);
            request.setQueryString(queryString);
        }

    }

    static void enrichRequestWithHeaders(SimpleHttpServletRequest request, String line) {
        String[] headerNameValuePairArray = line.split(": ");
        request.setHeader(headerNameValuePairArray[0], headerNameValuePairArray[1]);
    }
}
