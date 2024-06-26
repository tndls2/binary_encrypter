package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Forbidden."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Not allowed method."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error.");

    private final HttpStatus httpStatus;
    private final String message;
}
