package com.tj.email.repository;

import com.tj.email.model.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailConfigRepository extends JpaRepository<EmailConfig,Long> {

    List<EmailConfig> findByUserId(Long userId);
}
