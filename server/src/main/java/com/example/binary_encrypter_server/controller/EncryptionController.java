package com.example.binary_encrypter_server.controller;
import com.example.binary_encrypter_server.dto.ResponseMessage;
import com.example.binary_encrypter_server.dto.StatusCode;
import com.example.binary_encrypter_server.dto.response.DefaultResponse;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.service.EncryptionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.example.binary_encrypter_server.exceptions.CustomException;
@RestController
@RequiredArgsConstructor
@RequestMapping("/encryption")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EncryptionController {
    private final EncryptionService encryptionService;

    /**
     * 암호화 이력 전체 조회 API
     * @param page 페이지 번호 (default=0)
     * @param size 페이지별 item 수 (default=5)
     * @throws CustomException 입력된 page가 음수, item이 0 이하 값인 경우
     * @return 암호화 이력 리스트를 포함한 Page 객체
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAllEncryptionLogsOrderByDesc(
            @RequestParam(value="page", defaultValue="0") int page,
            @RequestParam(value="size", defaultValue ="5") int size) {
        Page<EncryptionLogResponseDTO> encryptionLogList = encryptionService.getAllEncryptionLogsOrderByDesc(
                page, size);
        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK,
                ResponseMessage.GET_ALL_ENCRYPTION_LOG_SUCCESS, encryptionLogList));
    }
}
