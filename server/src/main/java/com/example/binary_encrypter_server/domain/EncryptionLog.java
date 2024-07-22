package com.example.binary_encrypter_server.domain;

import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EncryptionLog {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "encryption_id")
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition="BLOB")  // 참고: https://kogle.tistory.com/250
    private byte[] iv;

    @Column(nullable = false)
    private String originFile;

    @Column(nullable = false)
    private String encryptedFile;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
