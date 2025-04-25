package com.tj.email.service;

import java.io.IOException;
import java.util.List;

import com.tj.email.exception.UserException;
import com.tj.email.model.EmailConfig;

public interface EmailConfigService {

	public EmailConfig createPost(EmailConfig emailConfig) throws IOException;

	public List<EmailConfig> getALlEmailConfig();

	public EmailConfig getEmailConfigById(Long id);

	public EmailConfig updateEmailConfig(Long configId, Long userId, EmailConfig emailConfig) throws UserException;

	public String deleteEmailConfig(Long configId, Long userId) throws UserException;

}
