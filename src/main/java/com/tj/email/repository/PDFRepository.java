package com.tj.email.repository;

import com.tj.email.model.PDF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFRepository extends JpaRepository<PDF,Long> {
}
