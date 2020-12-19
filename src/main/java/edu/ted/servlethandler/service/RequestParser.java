package edu.ted.servlethandler.service;

import edu.ted.servlethandler.entity.SimpleHttpServletRequest;
import edu.ted.servlethandler.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class RequestParser {

    private static final Pattern METHOD_AND_URL_PATTERN = Pattern.compile("^(?<method>[A-Z]+) (?<resource>[^ ]+) (?<version>[^ ]+)");


    private RequestParser() {
        throw new AssertionError("No com.study.util.RequestParser instances for you!");
    }

    public static SimpleHttpServletRequest parseRequestString(BufferedReader socketReader) {
        try {
            String requestString = readSocket(socketReader);
            if (requestString.isEmpty()) {
                return null;
            }
            return createRequest(requestString, socketReader);
        } catch (IOException e) {
            e.printStackTrace();
            //throw new ServerException(HttpResponseCode.INTERNAL_ERROR, new SimpleHttpServletRequest());
        }
        return null;
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

    static SimpleHttpServletRequest createRequest(String requestString, BufferedReader socketReader) {
        boolean headersEnd = false;
        SimpleHttpServletRequest request = new SimpleHttpServletRequest();
        String[] requestLines = requestString.split("\n");
        enrichRequestWithUrlAndMethod(request, requestLines[0]);
        for (int i = 1; i < requestLines.length; i++) {
            if (requestLines[i].startsWith("Host")) {
                enrichRequestWithHostAndPort(request, requestLines[i]);
            } else if ("\n".equals(requestLines[i]) || "\r\n".equals(requestLines[i])) {
                headersEnd = true;
            } else if (!headersEnd){
                enrichRequestWithHeaders(request, requestLines[i]);
            } else if (headersEnd){
                parseThenSetParameters(request, requestLines[i]);
            }
        }
        if ("POST".equals(request.getMethod())){
            parseThenSetParameters(request, getPostBody(socketReader, request));
        }
        return request;
    }

    private static String getPostBody(BufferedReader socketReader, SimpleHttpServletRequest request) {
        int bodySize = request.getIntHeader("Content-Length");
        char[] body = new char[bodySize];
        try {
            socketReader.read(body, 0, bodySize);
            return String.copyValueOf(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void enrichRequestWithHostAndPort(SimpleHttpServletRequest request, String requestLine) {
        String[] secondRequestLineParams = requestLine.split("[:]");
        //request.setHost(secondRequestLineParams[1].trim());
        //request.setPort(Integer.parseInt(secondRequestLineParams[2]));
    }

    static void enrichRequestWithUrlAndMethod(SimpleHttpServletRequest request, String requestLine) {
        Matcher requestMatcher = METHOD_AND_URL_PATTERN.matcher(requestLine);
        if (requestMatcher.find()) {
            String methodText = requestMatcher.group("method");
            String url = requestMatcher.group("resource");
            String version = requestMatcher.group("version");
            request.setRequestURI(url);
            request.setContextPath(url.substring(0, url.indexOf("/", 1)));
            //request.setVersion(version);
            request.setMethod(methodText);

            //TODO: regexp Parser?
            request.setServletPath(url.replace(request.getContextPath(), ""));
            //protocol
        } else {
            //throw new ServerException(HttpResponseCode.BAD_REQUEST, new HttpRequest());
        }
    }

    static void enrichRequestWithHeaders(SimpleHttpServletRequest request, String line) {
        String[] headerNameValuePairArray = line.split(": ");
        request.setHeader(headerNameValuePairArray[0], headerNameValuePairArray[1]);
    }
}
