package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

    @Mock
    EncryptionLogRepository encryptionLogRepository;

//    @InjectMocks
    EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        this.encryptionService = new EncryptionService(encryptionLogRepository);
    }

    @Test
    @DisplayName("암호화 이력 최신순 조회 - 페이지 크기 10, 첫 페이지")
    void getAllEncryptionLogsOrderByDesc() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);
        when(encryptionLogRepository.findAllByOrderByIdDesc(pageable)).thenReturn(Page.empty());
        // when
        Page<EncryptionLogResponseDTO> result = encryptionService.getAllEncryptionLogsOrderByDesc(page, size);
        // then
        assertEquals(0, result.getTotalElements());
    }


    @Test
    @DisplayName("AES-128 암호화")
    void encrypt() {
        // given
        byte[] content = "Test Content".getBytes();

        // when
        EncryptResponseDTO encryptResponseDTO = encryptionService.encrypt(content);

        // then
        assertNotNull(encryptResponseDTO);
        assertNotNull(encryptResponseDTO.getEncryptedContent());
        assertNotNull(encryptResponseDTO.getIv());
    }

    @Test
    @DisplayName("AES-128 암호화 - 잘못된 content 입력")
    void encrypt_InvalidContent() {
        // given
        byte[] content = null;

        // when, then
        assertThrows(CustomException.class, () -> encryptionService.encrypt(content),
                "암호화 실패 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("IV값 생성")
    void generateIV() {
        // when
        byte[] iv = EncryptionService.generateIV();

        // then
        assertNotNull(iv);
        assertEquals(16, iv.length);
    }

    @Test
    @DisplayName("SecretKey 생성")
    void getSecretKey() {
        // when
        SecretKey secretKey = EncryptionService.getSecretKey();

        // then
        assertNotNull(secretKey);
        assertEquals("AES", secretKey.getAlgorithm());
    }

    @Test
    @DisplayName("암호화 이력 생성")
    void createEncryptionLog() {
        // given
        EncryptionLogRequestDTO requestDTO = new EncryptionLogRequestDTO("origin.txt", "encrypted.txt", "123456");

        // when
        assertDoesNotThrow(() -> encryptionService.createEncryptionLog(requestDTO));
    }

    @Test
    @DisplayName("byte array -> 16진수 문자열 변환")
    void byteArrayToHexaString() {
        // given
        byte[] bytes = {10, 15, 20, 25};

        // when
        String hexString = EncryptionService.byteArrayToHexaString(bytes);

        // then
        assertNotNull(hexString);
        assertEquals("0A0F1419", hexString);
    }
}
