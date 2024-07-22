package com.example.binary_encrypter_server.exceptions;

import org.springframework.http.HttpStatus;

// 참고: https://mangkyu.tistory.com/205
public interface ErrorCode {
    String name();

    HttpStatus getHttpStatus();

    String getMessage();
}