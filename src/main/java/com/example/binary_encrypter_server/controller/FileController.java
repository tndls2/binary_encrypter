package com.example.binary_encrypter_server.controller;

import com.example.binary_encrypter_server.dto.ResponseMessage;
import com.example.binary_encrypter_server.dto.StatusCode;
import com.example.binary_encrypter_server.dto.response.DefaultResponse;
import com.example.binary_encrypter_server.dto.response.FileResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import com.example.binary_encrypter_server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;
    /*
     * 바이너리 파일 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) {
        // file 예외상황 처리
        // 파일이 여러개인 경우에 대한 처리
        // 파일명 중복 체크
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".bin")) {
            // 바이너리 파일만 업로드 가능
            try {
                // 업로드 파일 저장
                FileResponseDTO fileDto = fileService.uploadFile(file);
                return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPLOAD_FILE, fileDto));
            } catch (IOException e) {
                throw new CustomException(FileErrorCode.UPLOAD_FILE_FAIL);
            }
        } else {
            // 바이너리 파일이 아닌 경우 에러 발생
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only binary files are allowed.");
        }

    }

    /*
     * 바이너리 파일 다운로드: 파라미터의 파일명(name)으로 조회 후 다운로드
     */
    @GetMapping("/{name}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("name") String name) {
        // 파일 로드
        byte[] binaryFile = new byte[0];

        try {
            binaryFile = fileService.findFileByName(name);
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.DOWNLOAD_FILE_FAIL);
        }

        // HTTP 헤더 설정 (파일 다운로드를 위한 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", name);

        ResponseEntity<byte[]> response = new ResponseEntity<>(binaryFile, headers, HttpStatus.OK);

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.DOWNLOAD_FILE, response));
    }
}
