package com.example.binary_encrypter_server.controller;

import com.example.binary_encrypter_server.dto.ResponseMessage;
import com.example.binary_encrypter_server.dto.StatusCode;
import com.example.binary_encrypter_server.dto.response.DefaultResponse;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.service.EncryptionService;
import com.example.binary_encrypter_server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {
    private final FileService fileService;
    private final EncryptionService encryptionService;

    /**
     * 바이너리 파일 업로드 API
     * @param file 사용자가 업로드한 파일
     * @throws CustomException
     *          1. 업로드한 파일이 binary file이 아닌 경우
     *          2. 파일 경로에 파일이 존재하지 않는 경우
     *          3. 암호화에 실패한 경우
     *          4. 암호화 이력 저장에 실패한 경우
     *          5. 업로드한 파일을 특정 경로에 저장하는 것을 실패한 경우
     *          6. 파일 내용을 읽어오지 못한 경우
     *          7. 암호화한 파일을 특정 경로에 저장하는 것을 실패한 경우
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) {
        fileService.uploadFile(file);
        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPLOAD_FILE));
    }

    /**
     * 바이너리 파일 다운로드 API
     * @param name 다운로드할 파일명(확장자 포함)
     * @throws IOException
     *         파일을 읽어올 수 없는 경우
     * @throws CustomException 파일이 존재하지 않거나 읽을 수 없는 경우
     * @return
     *      1. 다운로드를 명시한 header
     *      2. 다운로드할 파일
     */
    @GetMapping("/{name}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String name) {
        Resource resource = fileService.downloadFile(name);

        // 파일 다운로드를 명시하는 header와 파일을 담은 body 리턴
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename:\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}