package com.tj.email.service;

import java.io.IOException;
import java.util.List;

import com.tj.email.exception.UserException;
import com.tj.email.model.EmailConfig;
import com.tj.email.model.dto.EmailConfigDTO;

public interface EmailConfigService {

	public EmailConfig createPost(EmailConfig emailConfig) throws IOException;

	public List<EmailConfigDTO> getALlEmailConfig(Long userId);

	public EmailConfig getEmailConfigById(Long id);

	public EmailConfig updateEmailConfig(Long configId, Long userId, EmailConfig emailConfig) throws UserException;

	public String deleteEmailConfig(Long configId, Long userId) throws UserException;


	public EmailConfigDTO mapToDTO(EmailConfig config) ;


}
