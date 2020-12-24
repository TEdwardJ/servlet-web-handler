package edu.ted.servlethandler.exception;

import lombok.Getter;

public class ServerException extends RuntimeException{

    @Getter
    private int responseCode;

    public ServerException(int httpStatus) {
        this.responseCode = httpStatus;
    }

    public ServerException(Exception e, int responseCode) {
        super(e);
        this.responseCode = responseCode;
    }

    public ServerException(String message, Throwable cause, int responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }
}
