package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode{
    INVALID_FILE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "업로드한 파일이 binary file이 아니거나 file이 선택되지 않았습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
