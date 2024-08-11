package com.example.binary_encrypter_server.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode{
    UPLOAD_FILE_TO_S3_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일을 S3에 업로드하는 중 오류가 발생했습니다."),
    FILE_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 도중 파일을 읽거나 변환하는 데 문제가 발생했습니다."),
    GET_FILE_FROM_S3_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3에서 파일을 가져올 수 없습니다."),
    GET_FILE_CONTENT_FROM_S3_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3에서 파일내용을 불러오는 데 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
