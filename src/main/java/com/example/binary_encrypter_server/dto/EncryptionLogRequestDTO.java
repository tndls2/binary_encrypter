package com.example.binary_encrypter_server.dto;

import com.example.binary_encrypter_server.domain.EncryptionLog;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionLogRequestDTO {
    private String originFile;
    private String encryptedFile;
    private String iv;

    public EncryptionLog toEntity() {
        return EncryptionLog.builder()
                .originFile(originFile)
                .encryptedFile(encryptedFile)
                .iv(iv)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
