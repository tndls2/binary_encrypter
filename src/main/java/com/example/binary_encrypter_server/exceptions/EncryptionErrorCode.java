package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EncryptionErrorCode implements ErrorCode{
    GET_CIPHER_FAIL(HttpStatus.NOT_FOUND, "해당 암호를 가져오지 못했습니다."),
    ENCRYPT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "암호화에 실패했습니다."),
    INVALID_PAGE_COMPONENT(HttpStatus.BAD_REQUEST, "유효하지 않은 page, size값 입니다."),
    IV_TYPE_CONVERSION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IV값의 형 변환하는 데 실패했습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
