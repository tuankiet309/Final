package com.lgcns.theseven.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an entity was not found.
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
