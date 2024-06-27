package com.example.binary_encrypter_server.infrastructure;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
@Transactional
class EncryptionLogRepositoryTest {

    @Autowired
    EncryptionLogRepository encryptionLogRepository;

    EncryptionLog log1;
    EncryptionLog log2;

    @BeforeEach
    void setUp() {
        // log1은 현재 시간에서 2초 전에 생성된 것으로 설정
        log1 = EncryptionLog.builder()
                .createdAt(LocalDateTime.now().minusSeconds(2))
                .originFile("origin.env")
                .encryptedFile("origin_enc.env")
                .iv("123456")
                .build();

        // log2는 현재 시간에서 1초 전에 생성된 것으로 설정
        log2 = EncryptionLog.builder()
                .createdAt(LocalDateTime.now().minusSeconds(1))
                .originFile("origin_2.env")
                .encryptedFile("origin_2_enc.env")
                .iv("123456")
                .build();
    }

    @Test
    @DisplayName("암호화 이력 최신순 조회 - 저장된 이력을 모두 출력한다.")
    void findAllByOrderByIdDescTotalElements() {
        // given
        encryptionLogRepository.save(log1);
        encryptionLogRepository.save(log2);

        // when
        Page<EncryptionLog> result = encryptionLogRepository.findAllByOrderByIdDesc(
                PageRequest.of(0, 10));  // 0번째 페이지, 아이템 10개로 설정

        // then
        assertNotNull(result, "결과는 null이 아니어야 합니다.");
        assertEquals(2, result.getTotalElements(), "결과의 총 개수는 2여야 합니다.");
    }

    @Test
    @DisplayName("암호화 이력 최신순 조회 - ID 내림차순으로 조회된다")
    void findAllByOrderByIdDesc() {
        // given
        encryptionLogRepository.save(log1);
        encryptionLogRepository.save(log2);

        // when
        Page<EncryptionLog> result = encryptionLogRepository.findAllByOrderByIdDesc(
                PageRequest.of(0, 10));  // 0번째 페이지, 아이템 10개로 설정

        // then
        assertEquals(log2.getId(), result.getContent().get(0).getId(),
                        "첫 번째 요소의 ID가 일치하지 않습니다.");
        assertEquals(log1.getId(), result.getContent().get(1).getId(),
                        "두 번째 요소의 ID가 일치하지 않습니다.");
    }
}
