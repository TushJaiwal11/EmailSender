package com.tj.email.repository;

import com.tj.email.model.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailConfigRepository extends JpaRepository<EmailConfig,Long> {
}
