package com.example.binary_encrypter_server.infrastructure;
import com.example.binary_encrypter_server.domain.EncryptionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncryptionLogRepository extends JpaRepository<EncryptionLog, Long> {
    List<EncryptionLog> findAllByOrderByIdDesc();
}