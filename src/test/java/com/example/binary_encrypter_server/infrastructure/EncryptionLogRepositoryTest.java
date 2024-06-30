package com.example.binary_encrypter_server.infrastructure;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    PageRequest pageable;  // 0번째 페이지, 아이템 10개로 임의 설정
    @BeforeEach
    void setUp() {
        // 생성 순서: log1 -> log2
        log1 = EncryptionLog.builder()
                .createdAt(LocalDateTime.now().minusSeconds(2))  // log1은 현재 시간에서 2초 전에 생성된 것으로 설정
                .originFile("origin.env")
                .encryptedFile("origin_enc.env")
                .iv(new byte[]{10, 15, 20, 25})
                .build();

        log2 = EncryptionLog.builder()
                .createdAt(LocalDateTime.now().minusSeconds(1))  // log2는 현재 시간에서 1초 전에 생성된 것으로 설정
                .originFile("origin_2.env")
                .encryptedFile("origin_2_enc.env")
                .iv(new byte[]{10, 15, 20, 25})
                .build();

    }

    @Nested
    @DisplayName("모든 EncryptionLog 데이터를 최신순으로 조회하는 메소드 테스트")
    class findAllByOrderByIdDesc{
        @Test
        @DisplayName("모든 데이터를 출력하는지 확인")
        void findAllByOrderByIdDescTotalElements() {
            // given
            encryptionLogRepository.save(log1);
            encryptionLogRepository.save(log2);
            pageable = PageRequest.of(0, 10);

            // when
            Page<EncryptionLog> result = encryptionLogRepository.findAllByOrderByIdDesc(pageable);

            // then
            assertNotNull(result, "결과는 null이 아니어야 합니다.");
            assertEquals(2, result.getTotalElements(), "결과의 총 개수는 2여야 합니다.");
        }

        @Test
        @DisplayName("최신순(id 내림차순)으로 데이터 정렬하는지 확인")
        void findAllByOrderByIdDescSort() {
            // given
            encryptionLogRepository.save(log1);
            encryptionLogRepository.save(log2);
            pageable = PageRequest.of(0, 10);


            // when
            Page<EncryptionLog> result = encryptionLogRepository.findAllByOrderByIdDesc(
                    PageRequest.of(0, 10));  // 0번째 페이지, 아이템 10개로 설정

            // then
            assertEquals(log2.getId(), result.getContent().get(0).getId(),
                            "첫 번째 요소의 ID가 일치하지 않습니다.");
            assertEquals(log1.getId(), result.getContent().get(1).getId(),
                            "두 번째 요소의 ID가 일치하지 않습니다.");
        }

        @Test
        @DisplayName("page size에 부합한 element 수를 조회하는지 확인")
        void findAllByOrderByIdDescPage() {
            // given
            encryptionLogRepository.save(log1);
            encryptionLogRepository.save(log2);
            pageable = PageRequest.of(0, 1);


            // when
            Page<EncryptionLog> result = encryptionLogRepository.findAllByOrderByIdDesc(pageable);

            // then
            assertTrue(pageable.getPageSize() >= result.getNumberOfElements(),
                    "페이지 크기가 데이터 수보다 크거나 같아야 합니다.");
        }
    }
}
