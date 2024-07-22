package com.example.binary_encrypter_server.dto;


import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
@Transactional
public class EncryptionLogResponseDTOTest {
    @Autowired
    private EncryptionLogResponseDTO encryptionLogResponseDTO;

    @Test
    @DisplayName("byte array -> 16진수 문자열 변환하는 메소드 테스트")
    void byteArrayToHexaString() {

        // given
        byte[] bytes = {10, 15, 20, 25};

        // when
        String hexString = encryptionLogResponseDTO.byteArrayToHexString(bytes);

        // then
        assertNotNull(hexString);
        assertEquals("0A0F1419", hexString);
    }
}
