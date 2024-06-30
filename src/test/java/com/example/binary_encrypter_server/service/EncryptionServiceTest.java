package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.EncryptionErrorCode;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@Transactional
@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {
    private static final int IV_LENGTH = 16;

    @Mock
    EncryptionLogRepository encryptionLogRepository;
    @Autowired
    @InjectMocks
    EncryptionService encryptionService;

    @Value("${aes.key}")
    private String testSecretKeyString;  //application-test.properties 참조

    @BeforeEach
    void setUp() {
        // encryptionService 객체의 SECRET_KEY_STRING 필드 주입
        ReflectionTestUtils.setField(encryptionService, "SECRET_KEY_STRING", testSecretKeyString);
    }

    @Nested
    @DisplayName("암호화 이력 최신순 조회하는 메소드 테스트")
    class getAllEncryptionLogsOrderByDesc {
        @Test
        @DisplayName("입력된 page와 size가 조건에 부합하면 조회 실행")
        void getAllEncryptionLogsOrderByDescSuccess() {
            // given
            int page = 0;
            int size = 5;
            PageRequest pageable = PageRequest.of(page, size);

            // when
            Page<EncryptionLogResponseDTO> result = encryptionService.getAllEncryptionLogsOrderByDesc(page, size);

            // then
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("입력된 page와 size가 조건에 부합하지 않으면 INVALID_PAGE_COMPONENT 발생")
        void getAllEncryptionLogsOrderByDescFail() {
            // given
            int page = -1;
            int size = -1;

            // when, then
            assertThatThrownBy(() -> encryptionService.getAllEncryptionLogsOrderByDesc(page, size))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EncryptionErrorCode.INVALID_PAGE_COMPONENT);
        }
    }

    @Test
    @DisplayName("주어진 content를 AES-128 암호화하는 메소드 테스트")
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
    @DisplayName("ipher 가져오는 메소드 테스트")
    void getCipherInstance(){
        assertDoesNotThrow(() -> encryptionService.getCipherInstance());
    }

    @Test
    @DisplayName("IV값 랜덤 생성 메소드 테스트")
    void generateIV() {
        // given, when
        byte[] iv = EncryptionService.generateIV();

        // then
        assertNotNull(iv);
        assertEquals(IV_LENGTH, iv.length);
    }

    @Test
    @DisplayName("알고리즘에 부합한 형태의 키 생성 메소드 테스트")
    void getSecretKey() {
        // given, when
        SecretKey secretKey = encryptionService.getSecretKey(testSecretKeyString);

        // then
        assertNotNull(secretKey);
    }

    @Nested
    @DisplayName("Cipher로 암호화 진행하는 메소드 테스트")
    class useCipher {
        @Test
        @DisplayName("Cipher로 암호화된 내용이 반환됨.")
        void useCipherSuccess() {
            //given
            Cipher cipher = encryptionService.getCipherInstance(); // Cipher 가져오기
            SecretKey key = encryptionService.getSecretKey(testSecretKeyString); // key 생성
            byte[] iv = encryptionService.generateIV(); // IV 생성
            byte[] content = "test".getBytes();

            //when
            byte[] encryptedContent = encryptionService.useCipher(cipher, key, iv, content);

            //then
            assertNotNull(encryptedContent);
            assertNotEquals(content, encryptedContent);

        }

        @Test
        @DisplayName("Cipher에 잘못 값을 기입하면 ENCRYPT_FAIL 발생")
        void useCipherFaile() {
            //given
            Cipher cipher = encryptionService.getCipherInstance();
            SecretKey key = encryptionService.getSecretKey(testSecretKeyString);
            byte[] iv = encryptionService.generateIV();
            byte[] content = null; // content가 null값이 들어가는 오류 발생

            // when, then
            assertThatThrownBy(() -> encryptionService.useCipher(cipher, key, iv, content))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", EncryptionErrorCode.ENCRYPT_FAIL);

        }
    }
    @Test
    @DisplayName("암호화 이력 생성하는 메소드 테스트")
    void createEncryptionLog() {
        // given
        EncryptionLogRequestDTO requestDTO = new EncryptionLogRequestDTO("origin.txt", "encrypted.txt", new byte[]{10, 15, 20, 25});

        // when, then
        assertDoesNotThrow(() -> encryptionService.createEncryptionLog(requestDTO));
    }
}
