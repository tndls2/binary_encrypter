package com.example.binary_encrypter_server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionResponseDTO {
    private byte[] encryptedContent;
    private String iv;
}
