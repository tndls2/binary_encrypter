package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import com.example.binary_encrypter_server.dto.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.EncryptionResponseDTO;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private final EncryptionLogRepository encryptionLogRepository;
    // AES 암호 알고리즘, 암호 운용 방식 : CBC 모드, 패딩 기법 : PKCS7 고정
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_STRING = "0123456789abcdef"; // AES-128(128bits)의 비밀키는 임의의 16byte
    private static final int IV_LENGTH = 16; // IV 길이는 AES의 블록 크기와 같아야 합니다 (여기서는 128비트 = 16바이트)

    // AES 키 생성 메서드
    public static SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
    }
    // IV값 랜덤 생성 메서드
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /* 암호화 */
    public EncryptionResponseDTO encrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = generateIV(); // IV 생성

        String ivBase64 = Base64.getEncoder().withoutPadding().encodeToString(iv);
        System.out.println("Generated IV (Base64): " + ivBase64); // IV를 Base64로 인코딩하여 출력

        SecretKey key = getSecretKey();

        // 암호화할 때 IV와 key를 함께 설정
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        // 암호화된 데이터
        byte[] encryptedContent = cipher.doFinal(content);

        // EncryptionLog Response DTO 생성
        EncryptionResponseDTO encryptionDTO = new EncryptionResponseDTO(encryptedContent, new String(iv));

        return encryptionDTO;
    }

    /* 암호화 이력 조회 */
    public List<EncryptionLog> getAllEncryptionLogsOrderByDesc() {
        // 가장 최신 이력부터 정렬 (내림차순)
        List<EncryptionLog> encryptionLogList = encryptionLogRepository.findAllByOrderByIdDesc();
        return encryptionLogList;
    }

    /* 암호화 이력 생성 */
    public Long createEncryptionLog(EncryptionLogRequestDTO requestDTO) {
        EncryptionLog encryptionLog = encryptionLogRepository.save(requestDTO.toEntity());
        return encryptionLog.getId();
    }
}
