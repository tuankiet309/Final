package com.lgcns.theseven.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for invalid client requests.
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
