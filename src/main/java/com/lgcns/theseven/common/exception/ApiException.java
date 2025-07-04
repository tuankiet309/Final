package com.lgcns.theseven.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base runtime exception carrying an HTTP status.
 */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
