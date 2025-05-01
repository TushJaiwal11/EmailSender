package com.tj.email.model.dto;

import lombok.Data;

@Data
public class EmailConfigDTO {

    private Long id;
    private String email;
    private String mailBody;
    private String appPassword;
    private String subject;

    // You can add userId if needed (but not the full User object to avoid circular references)
    private Long userId;
}
