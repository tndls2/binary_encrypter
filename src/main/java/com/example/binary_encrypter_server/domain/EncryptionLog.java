package com.example.binary_encrypter_server.domain;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String iv;

    @Column(nullable = false)
    private String originFile;

    @Column(nullable = false)
    private String encryptedFile;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
