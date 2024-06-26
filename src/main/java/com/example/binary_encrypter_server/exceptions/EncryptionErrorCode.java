package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class EncryptionErrorCode implements ErrorCode{

    private final HttpStatus httpStatus;
    private final String message;
}
