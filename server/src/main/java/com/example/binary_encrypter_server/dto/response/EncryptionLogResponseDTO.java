package com.example.binary_encrypter_server.dto.response;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import com.example.binary_encrypter_server.exceptions.CommonErrorCode;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.EncryptionErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionLogResponseDTO {
    private Long id;
    private String originFile;
    private String encryptedFile;
    private String iv;

    private String createdAt;

    public static EncryptionLogResponseDTO fromEntity(EncryptionLog log) {
        // datetime 포멧
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // byte array -> Hex String 변환
        byte[] ivBytes = log.getIv();
        String ivHexString = byteArrayToHexString(ivBytes);

        return new EncryptionLogResponseDTO(
                log.getId(),
                log.getOriginFile(),
                log.getEncryptedFile(),
                ivHexString,
                log.getCreatedAt().format(formatter)
        );
    }

    /**
     * byte array -> 16진수로 변환하여 반환*
     * @param bytes byte array 값
     * @return hex 값
     */
    public static String byteArrayToHexString(byte[] bytes) {
        // 참고: https://3edc.tistory.com/21
        try {
            StringBuilder builder = new StringBuilder();
            for (byte data : bytes) {
                builder.append(String.format("%02X", data));
            }
            return builder.toString();
        } catch(Exception e){
            throw new CustomException(EncryptionErrorCode.IV_TYPE_CONVERSION_FAIL);
        }
    }
}
