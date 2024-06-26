package com.example.binary_encrypter_server.exceptions;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();

    String getMessage();
}
