
package com.tj.email.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tj.email.mapper.Mapper;
import com.tj.email.model.dto.EmailConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tj.email.exception.UserException;
import com.tj.email.model.EmailConfig;
import com.tj.email.repository.EmailConfigRepository;
import com.tj.email.service.EmailConfigService;

@Service
public class EmailConfigServiceImpl implements EmailConfigService {

	@Autowired
	private EmailConfigRepository emailConfigRepository;



	@Override
	public EmailConfig createPost(EmailConfig emailConfig) throws IOException {

		return emailConfigRepository.save(emailConfig);
	}

	@Override
	public List<EmailConfigDTO> getALlEmailConfig(Long userId) {
		List<EmailConfig> configs = emailConfigRepository.findByUserId(userId);

		List<EmailConfigDTO> collect = configs.stream().map(this::mapToDTO).collect(Collectors.toList());
		return collect;
	}

	@Override
	public EmailConfig getEmailConfigById(Long id) {

		Optional<EmailConfig> emailConfig = emailConfigRepository.findById(id);
		return emailConfig.get();
	}

	@Override
	public EmailConfig updateEmailConfig(Long configId, Long userId, EmailConfig emailConfig) throws UserException {

		EmailConfig existingEmailConfig = getEmailConfigById(configId);

		if (existingEmailConfig != null && existingEmailConfig.getUser().getId().equals(userId)) {
			existingEmailConfig.setEmail(emailConfig.getEmail());
			existingEmailConfig.setAppPassword(emailConfig.getAppPassword());
			existingEmailConfig.setMailBody(emailConfig.getMailBody());
			existingEmailConfig.setSubject(emailConfig.getSubject());

			return emailConfigRepository.save(existingEmailConfig);
		}

		throw new UserException("Try again");
	}

	@Override
	public String deleteEmailConfig(Long configId, Long userId) throws UserException {

		EmailConfig existingEmailConfig = getEmailConfigById(configId);

		if (existingEmailConfig != null && existingEmailConfig.getUser().getId().equals(userId)) {
			emailConfigRepository.deleteById(configId);
			return "Email Configuration has been deleted successfully.....";
		}

		throw new UserException("Please contact your admin");
	}

	@Override
	public EmailConfigDTO mapToDTO(EmailConfig config) {
		EmailConfigDTO dto = new EmailConfigDTO();
		dto.setId(config.getId());
		dto.setEmail(config.getEmail());
		dto.setAppPassword(config.getAppPassword());
		dto.setSubject(config.getSubject());
		dto.setMailBody(config.getMailBody());
		dto.setUserId(config.getUser().getId());
		return dto;
	}

}
