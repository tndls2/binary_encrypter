package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptionLogResponseDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.EncryptionErrorCode;
import com.example.binary_encrypter_server.infrastructure.EncryptionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
/**
 * 암호화, 암호화 이력 관련 작업을 처리하는 서비스
 */
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;
    @Value("${aes.key}")
    private String SECRET_KEY_STRING;
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
     * AES-128 암호화
     *  1. AES-128 암호 설정
     *  2. 암호화
     *  3. IV 형변환
     * @param content 파일 내용
     * @return
     *      1. 암호화된 내용
     *      2. IV값
     */
    @Transactional(readOnly = false)
    public EncryptResponseDTO encrypt(byte[] content) {
        Cipher cipher = getCipherInstance(); // Cipher 가져오기
        SecretKey key = getSecretKey(SECRET_KEY_STRING); // key 생성
        byte[] iv = generateIV(); // IV 생성

        // 암호화
        byte[] encryptedContent = useCipher(cipher, key, iv, content);

        // IV를 16진수 문자열로 변환(db 저장에 적절한 형태로 변환)
        String stringIV = byteArrayToHexaString(iv);

        // EncryptionLog Response DTO 생성
        return new EncryptResponseDTO(encryptedContent, stringIV);
    }

    /**
     * Cipher 가져오기
     * @throws CustomException 암호를 가져오지 못한 경우
     * @return cipher 객체
     */
    public Cipher getCipherInstance(){
        try {
            return Cipher.getInstance(TRANSFORMATION);
        } catch (Exception e) {
            throw new CustomException(EncryptionErrorCode.GET_CIPHER_FAIL);
        }
    }
    /**
     * IV값 랜덤 생성
     * @return iv iv값
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 알고리즘에 부합한 형태의 키 생성
     * @return key
     */
    public SecretKey getSecretKey(String stringSecretKey) {
        SecretKeySpec key = new SecretKeySpec(stringSecretKey.getBytes(), ALGORITHM);
        return key;
    }

    /**
     * Cipher로 암호화 진행
     * @param cipher 사용할 cipher
     * @param key key값
     * @param iv iv값
     * @param content 암호화할 내용
     * @throws CustomException 암호화에 실패한 경우
     * @return 암호화된 내용
     */
    public byte[] useCipher(Cipher cipher, SecretKey key, byte[] iv, byte[] content){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));  // IV와 key를 cipher에 설정
            byte[] encryptedContent = cipher.doFinal(content); // 암호화
            return encryptedContent;
        } catch (Exception e) {
            throw new CustomException(EncryptionErrorCode.ENCRYPT_FAIL);
        }
    }

    /**
     * 암호화 이력 생성
     * @param requestDTO 암호화 전 파일명, 암호화 후 파일명, IV값
     */
    public void createEncryptionLog(EncryptionLogRequestDTO requestDTO) {
        encryptionLogRepository.save(requestDTO.toEntity());
    }

    /**
     * byte array -> 16진수로 변환하여 반환
     *
     * 참고: https://3edc.tistory.com/21
     *
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
}
