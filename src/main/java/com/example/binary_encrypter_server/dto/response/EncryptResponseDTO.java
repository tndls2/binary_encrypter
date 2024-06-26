package com.example.binary_encrypter_server.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptResponseDTO {
    private byte[] encryptedContent;
    private String iv;
}
