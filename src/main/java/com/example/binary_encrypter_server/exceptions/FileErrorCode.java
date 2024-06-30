package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode{
    UPLOAD_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    SAVE_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다."),
    DOWNLOAD_FILE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),
    INVALID_FILE_PATH(HttpStatus.NOT_FOUND, "파일 경로에서 해당 파일을 가져올 수 없습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "해당 파일을 가져올 수 없습니다."),

    INVALID_FILE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "업로드한 파일이 binary file이 아니거나 file이 선택되지 않았습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
