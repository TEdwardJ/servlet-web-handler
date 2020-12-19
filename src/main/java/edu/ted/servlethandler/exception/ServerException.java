package edu.ted.servlethandler.exception;


import edu.ted.servlethandler.entity.SimpleHttpServletRequest;

public class ServerException extends RuntimeException{

    private SimpleHttpServletRequest request;
    //private HttpResponseCode responseCode;
    private String responseCode;

    public ServerException(String httpStatus) {
        this.responseCode = httpStatus;
    }

    public ServerException(String responseCode, SimpleHttpServletRequest request) {
        this.request = request;
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public SimpleHttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(SimpleHttpServletRequest request) {
        this.request = request;
    }
}
