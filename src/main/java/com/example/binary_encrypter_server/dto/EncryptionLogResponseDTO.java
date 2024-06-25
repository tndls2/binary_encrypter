package com.example.binary_encrypter_server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionLogResponseDTO {
    private String originFile;
    private String encryptedFile;
    private String iv;
}
