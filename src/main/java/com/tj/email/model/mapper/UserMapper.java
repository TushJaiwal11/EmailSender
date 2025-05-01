package com.tj.email.model.mapper;

import com.tj.email.model.User;
import com.tj.email.model.dto.EmailConfigDTO;
import com.tj.email.model.dto.UserDto;

import java.util.List;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setGender(user.getGender());
        dto.setReferralCode(user.getReferralCode());
        dto.setJobRole(user.getJobRole());
        dto.setReferredBy(user.getReferredBy());
        dto.setPlanType(user.getPlanType());
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());
        dto.setLastLogin(user.getLastLogin());
        dto.setRec_status(user.getRec_status());
        dto.setActiveSubscription(user.getActiveSubscription());
        dto.setPasswordValidity(user.getPasswordValidity());
        dto.setSubscriptionPurchaseDate(user.getSubscriptionPurchaseDate());
        dto.setSubscriptionExpiryDate(user.getSubscriptionExpiryDate());

        if (user.getEmailConfig() != null) {
            List<EmailConfigDTO> emailDtos = user.getEmailConfig().stream().map(config -> {
                EmailConfigDTO configDto = new EmailConfigDTO();
                configDto.setId(config.getId());
                configDto.setEmail(config.getEmail());
                configDto.setMailBody(config.getMailBody());
                configDto.setAppPassword(null); // Mask sensitive field
                configDto.setSubject(config.getSubject());
                configDto.setUserId(user.getId());
                return configDto;
            }).toList();
            dto.setEmailConfigs(emailDtos);
        }
        return dto;
    }
}

