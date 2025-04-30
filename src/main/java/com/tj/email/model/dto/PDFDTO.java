package com.tj.email.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PDFDTO {

    private String title;
    private String pdfData;
    private LocalDateTime createAt;
}
