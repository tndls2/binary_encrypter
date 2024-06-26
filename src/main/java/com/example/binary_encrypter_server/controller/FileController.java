package com.example.binary_encrypter_server.controller;

import com.example.binary_encrypter_server.dto.ResponseMessage;
import com.example.binary_encrypter_server.dto.StatusCode;
import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.DefaultResponse;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.FileResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import com.example.binary_encrypter_server.service.EncryptionService;
import com.example.binary_encrypter_server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileController {
    private final FileService fileService;
    private final EncryptionService encryptionService;

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
                // 1. 해당 파일 내용 암호화
                EncryptResponseDTO encryptResponseDto = encryptionService.encrypt(fileDto.getContent());

                // 2. 새로운 이름 생성
                String originName = fileDto.getFileName();
                String newName = fileService.createFileName(originName, "_enc");

                // 3. 새로운 파일 생성
                FileRequestDTO newFileRequestDTO = new FileRequestDTO(newName, encryptResponseDto.getEncryptedContent());

                FileResponseDTO new_file_dto;
                try {
                    new_file_dto = fileService.createFile(newFileRequestDTO);
                } catch (IOException e) {
                    throw new CustomException(FileErrorCode.CREATE_FILE_FAIL);
                }

                // 4. 암호화 이력 생성
                EncryptionLogRequestDTO requestDTO = new EncryptionLogRequestDTO(
                        originName, newName, encryptResponseDto.getIv()
                );

                EncryptionLogResponseDTO encryptionLogResponseDTO = encryptionService.createEncryptionLog(requestDTO);


                return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.UPLOAD_FILE, fileDto));

            } catch (Exception e) {
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
    public ResponseEntity<Resource> downloadFile(@PathVariable String name) throws IOException {
        Resource resource = fileService.fileDownload(name);
        File file = resource.getFile();

        // Tika를 사용해서 파일의 MediaType을 알아낸다
        Tika tika = new Tika();
        String mediaType = tika.detect(file);

        // 파일 다운로드를 위한 헤더와 본문에 파일을 넣어서 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename:\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mediaType)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(resource);
    }
}