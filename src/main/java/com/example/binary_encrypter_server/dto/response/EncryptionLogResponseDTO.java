package com.example.binary_encrypter_server.dto.response;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionLogResponseDTO {
    private Long id;
    private String originFile;
    private String encryptedFile;
    private String iv;

    private String createdAt;

    public static EncryptionLogResponseDTO fromEntity(EncryptionLog log) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // datetime 포멧
        return new EncryptionLogResponseDTO(
                log.getId(),
                log.getOriginFile(),
                log.getEncryptedFile(),
                log.getIv(),
                log.getCreatedAt().format(formatter)
        );
    }
}
