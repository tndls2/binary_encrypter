package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode{
    UPLOAD_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    CREATE_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 생성에 실패했습니다."),
    DOWNLOAD_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
