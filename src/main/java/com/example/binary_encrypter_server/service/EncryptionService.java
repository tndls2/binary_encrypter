package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_STRING = "0123456789abcdef"; // AES-128(128bits)의 비밀키는 임의의 16byte
    private static final int IV_LENGTH = 16; // IV 길이는 AES의 블록 크기와 같아야 합니다 (여기서는 128비트 = 16바이트)
    private final EncryptionLogRepository encryptionLogRepository;

    // AES 키 생성 메서드
    private static SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
    }

    // IV값 랜덤 생성 메서드
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // byte array -> 16진수로 변환하여 반환
    // [출처: https://3edc.tistory.com/21]
    public static String byteArrayToHexaString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte data : bytes) {
            builder.append(String.format("%02X", data));
        }
        return builder.toString();
    }

    // 암호화
    @Transactional(readOnly = false)
    public EncryptResponseDTO encrypt(byte[] content) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = generateIV(); // IV 생성

        SecretKey key = getSecretKey();

        // 암호화할 때 IV와 key를 함께 설정
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        // 암호화된 데이터
        byte[] encryptedContent = cipher.doFinal(content);

        // IV를 16진수 문자열로 변환
        String stringIV = byteArrayToHexaString(iv);

        // EncryptionLog Response DTO 생성
        return new EncryptResponseDTO(encryptedContent, stringIV);
    }

    // 암호화 이력 조회
    public List<EncryptionLogResponseDTO> getAllEncryptionLogsOrderByDesc() {
        List<EncryptionLog> encryptionLogList = encryptionLogRepository.findAllByOrderByIdDesc();
        return encryptionLogList.stream()
                .map(EncryptionLogResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    // 암호화 이력 생성
    public Long createEncryptionLog(EncryptionLogRequestDTO requestDTO) {
        EncryptionLog encryptionLog = encryptionLogRepository.save(requestDTO.toEntity());
        return encryptionLog.getId();
    }
}
