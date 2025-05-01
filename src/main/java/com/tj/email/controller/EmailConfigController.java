package com.tj.email.controller;

import java.io.IOException;
import java.util.List;

import com.tj.email.model.dto.EmailConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tj.email.exception.UserException;
import com.tj.email.model.EmailConfig;
import com.tj.email.model.User;
import com.tj.email.repository.EmailConfigRepository;
import com.tj.email.service.EmailConfigService;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/")
public class EmailConfigController {

	@Autowired
	private EmailConfigService emailConfigService;

	@Autowired
	private EmailConfigRepository emailConfigRepository;

	@Autowired
	private UserService userService;

	@PostMapping("/upload")
	public ResponseEntity<EmailConfig> uploadResume(@RequestParam("email") String email,
			@RequestParam("subject") String subject, @RequestParam("mailBody") String mailBody,
			@RequestParam("appPassword") String appPassword, @RequestHeader("Authorization") String jwt)
			throws IOException, UserException {

		User profile = userService.getProfile(jwt);

		EmailConfig config = new EmailConfig();
		config.setEmail(email);
		config.setSubject(subject);
		config.setMailBody(mailBody);
		config.setAppPassword(appPassword);
		config.setUser(profile);

		emailConfigRepository.save(config);

		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	@GetMapping("/getAllMailConfig")
	public ResponseEntity<List<EmailConfigDTO>> getAllEmailConfigs(@RequestHeader("Authorization") String jwt)
			throws UserException {
		User profile = userService.getProfile(jwt);
		List<EmailConfigDTO> configs = emailConfigService.getALlEmailConfig(profile.getId());

		return ResponseEntity.ok(configs);
	}

	@PostMapping("/updateConfig/{configId}")
	public ResponseEntity<EmailConfig> updateEmailConfigHandler(@RequestHeader("Authorization") String jwt,
			@RequestBody EmailConfig emailConfig, @PathVariable Long configId) throws UserException {
		User profile = userService.getProfile(jwt);
		EmailConfig emailConfig1 = emailConfigService.updateEmailConfig(configId, profile.getId(), emailConfig);

		return new ResponseEntity<>(emailConfig1, HttpStatus.OK);
	}

	@DeleteMapping("/deleteConfig/{configId}")
	public ResponseEntity<String> updateEmailConfigHandler(@RequestHeader("Authorization") String jwt,
			@PathVariable Long configId) throws UserException {
		User profile = userService.getProfile(jwt);
		String emailConfig1 = emailConfigService.deleteEmailConfig(configId, profile.getId());

		return new ResponseEntity<>(emailConfig1, HttpStatus.OK);
	}

	@GetMapping("/getMailConfig/{configId}")
	public ResponseEntity<EmailConfig> getEmailConfigs(@RequestHeader("Authorization") String jwt,
			@PathVariable Long configId) throws UserException {
		User profile = userService.getProfile(jwt);
		EmailConfig configs = emailConfigService.getEmailConfigById(configId);

		return ResponseEntity.ok(configs);
	}

}
