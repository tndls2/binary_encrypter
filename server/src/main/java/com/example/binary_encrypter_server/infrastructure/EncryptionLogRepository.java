package com.example.binary_encrypter_server.infrastructure;
import com.example.binary_encrypter_server.domain.EncryptionLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;

public interface EncryptionLogRepository extends JpaRepository<EncryptionLog, Long> {
    Page<EncryptionLog> findAllByOrderByIdDesc(Pageable pageable);
}
