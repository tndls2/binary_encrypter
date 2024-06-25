package com.example.binary_encrypter_server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileRequestDTO {
    private String fileName;
    private byte[] content;

}
