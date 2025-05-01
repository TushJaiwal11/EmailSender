package com.tj.email.model.dto;


import java.time.LocalDateTime;
import java.util.List;

import com.tj.email.model.domain.PlanType;
import com.tj.email.model.domain.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private String gender;
    private String referralCode;
    private String jobRole;
    private Long referredBy;
    private PlanType planType;
    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;
    private Long rec_status;
    private Long activeSubscription;
    private LocalDateTime passwordValidity;
    private LocalDateTime subscriptionPurchaseDate;
    private LocalDateTime subscriptionExpiryDate;
    private List<EmailConfigDTO> emailConfigs;
}

