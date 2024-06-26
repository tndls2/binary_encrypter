package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.EncryptionErrorCode;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_STRING = "0123456789abcdef";
    private static final int IV_LENGTH = 16;
    private final EncryptionLogRepository encryptionLogRepository;

    /**
     * 암호화 이력 최신순 조회
     * @param page 페이지 번호
     * @param size 페이지별 item 수
     * @throws CustomException 입력된 page가 음수, item이 0 이하 값인 경우
     * @return 암호화 이력 리스트를 포함한 Page 객체
     */
    public Page<EncryptionLogResponseDTO> getAllEncryptionLogsOrderByDesc(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new CustomException(EncryptionErrorCode.INVALID_PAGE_COMPONENT);
        }
        PageRequest pageable = PageRequest.of(page, size);
        return encryptionLogRepository.findAllByOrderByIdDesc(pageable)
                .map(EncryptionLogResponseDTO::fromEntity);
    }

    /**
     * byte array -> 16진수로 변환하여 반환
     * 출처: https://3edc.tistory.com/21
     * @param bytes byte array 값
     * @return hex 값
     */
    public static String byteArrayToHexaString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte data : bytes) {
            builder.append(String.format("%02X", data));
        }
        return builder.toString();
    }

    /**
     * AES-128 암호화
     *  1. AES-128 암호 설정
     *  2. 암호화
     *  3. IV 형변환
     * @param content 파일 내용
     * @throws CustomException
     *          1. 암호를 가져오지 못한 경우
     *          2. 암호에 유효하지 않은 key 또는 IV을 사용한 경우
     *          3. 암호화 실패한 경우
     * @return
     *      1. 암호화된 내용
     *      2. IV값
     */
    @Transactional(readOnly = false)
    public EncryptResponseDTO encrypt(byte[] content) {
        // 암호 가져오기
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (Exception e) {
            throw new CustomException(EncryptionErrorCode.GET_CIPHER_FAIL);
        }

        byte[] iv = generateIV(); // IV 생성
        SecretKey key = getSecretKey(); // key 생성

        // IV와 key를 cipher에 설정
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new CustomException(EncryptionErrorCode.INVALID_CIPHER_PARAMETER);
        }

        // 암호화
        byte[] encryptedContent = new byte[0];
        try {
            encryptedContent = cipher.doFinal(content);
        } catch (Exception e) {
            throw new CustomException(EncryptionErrorCode.ENCRYPT_FAIL);
        }

        // IV를 16진수 문자열로 변환(db 저장에 적절한 형태로 변환)
        String stringIV = byteArrayToHexaString(iv);

        // EncryptionLog Response DTO 생성
        return new EncryptResponseDTO(encryptedContent, stringIV);
    }

    /**
     * IV값 랜덤 생성
     * @return iv iv값
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 알고리즘에 부합한 형태의 키 생성
     * @return key
     */
    private static SecretKey getSecretKey() {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
        return key;
    }

    /**
     * 암호화 이력 생성
     * @param requestDTO 암호화 전 파일명, 암호화 후 파일명, IV값
     */
    public void createEncryptionLog(EncryptionLogRequestDTO requestDTO) {
        encryptionLogRepository.save(requestDTO.toEntity());
    }
}
