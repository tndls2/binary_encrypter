package com.example.binary_encrypter_server.controller;
import com.example.binary_encrypter_server.dto.ResponseMessage;
import com.example.binary_encrypter_server.dto.StatusCode;
import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.DefaultResponse;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.FileResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import com.example.binary_encrypter_server.service.EncryptionService;
import com.example.binary_encrypter_server.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/encryption")
public class EncryptionController {
    private final EncryptionService encryptionService;
    private final FileService fileService;

    /*
     * 암호화 이력 전체 조회
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllEncryptionLogsOrderByDesc(@RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="size", defaultValue ="5") int size) {
        Page<EncryptionLogResponseDTO> encryptionLogList = encryptionService.getAllEncryptionLogsOrderByDesc(page, size);
        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.GET_ALL_ENCRYPTION_LOG_SUCCESS, encryptionLogList));
    }

    /*
     * 특정 파일 암호화: EncryptionLog 객체 생성
     */
    @PostMapping("/")
    public ResponseEntity<?> encryptContent(@RequestBody FileRequestDTO fileRequestDTO) throws Exception {
        // 1. 해당 파일 내용 암호화
        EncryptResponseDTO encryptResponseDto = encryptionService.encrypt(fileRequestDTO.getContent());

        // 2. 새로운 이름 생성
        String originName = fileRequestDTO.getFileName();
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

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.ENCRYPT_CONTENT_SUCCESS, new_file_dto));
    }

}
