package com.example.binary_encrypter_server.controller;
import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.service.EncryptionService;
import com.example.binary_encrypter_server.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    public List<EncryptionLogResponseDTO> getAllEncryptionLogsOrderByDesc() {
        List<EncryptionLogResponseDTO> encryptionLogList = encryptionService.getAllEncryptionLogsOrderByDesc();
        return encryptionLogList;
    }

    /*
     * 특정 파일 암호화: EncryptionLog 객체 생성
     */
    @PostMapping("/")
    public ResponseEntity<?> encryptContent(@RequestBody FileRequestDTO fileRequestDTO) throws Exception {
        // 1. 해당 파일 내용 암호화
        EncryptResponseDTO encryptResponseDto = encryptionService.encrypt(fileRequestDTO.getContent());

        // 3. 암호화한 내용과 새로운 이름으로 새로운 파일 저장
        String originName = fileRequestDTO.getFileName();
        String newName = fileService.createFileName(originName, "_enc");

        // 4. 암호화 이력 생성
        EncryptionLogRequestDTO requestDTO = new EncryptionLogRequestDTO(
                originName, newName, encryptResponseDto.getIv()
        );
        Long encryptionLogId = encryptionService.createEncryptionLog(requestDTO);
        return ResponseEntity.ok("File Encryption successfully: " + encryptionLogId);
    }

}
