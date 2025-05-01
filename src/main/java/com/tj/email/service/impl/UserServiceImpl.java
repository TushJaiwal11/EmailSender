package com.tj.email.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.tj.email.model.EmailConfig;
import com.tj.email.model.dto.UserDto;
import com.tj.email.model.mapper.UserMapper;
import com.tj.email.repository.EmailConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tj.email.config.JwtProvider;
import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.model.domain.UserRole;
import com.tj.email.repository.UserRepository;
import com.tj.email.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailConfigRepository emailConfigRepository;

	@Override
	public UserDto getProfile(String jwt) throws UserException {

		String email = JwtProvider.getEmailFromJwtToken(jwt);
		if (email == null) {
			throw new UserException("email not fount :");
		}
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserException("user not fount :");
		}


		List<EmailConfig> byUserId = emailConfigRepository.findByUserId(user.getId());
		user.setEmailConfig(byUserId);
		return UserMapper.toDto(user);
	}

	@Override
	public User getProfileById(Long userId) throws UserException {
		User user = userRepository.findById(userId).get();

		if (user != null) {
			return user;
		}

		throw new UserException("User Not found with id :" + userId);
	}

	@Override
	public List<User> getAllUser(Long userId) throws UserException {

		User profile = getProfileById(userId);

		if (profile.getRole().equals(UserRole.ROLE_ADMIN)) {
			List<User> users = userRepository.findAll();
			return users;
		}
		throw new UserException("Only admin can Achive the user list ");
	}

	@Override
	public User updateUser(User user, Long userid) throws UserException {

		User existingUser = userRepository.findById(userid).orElse(null);

		if (existingUser != null && user.getId().equals(existingUser.getId())) {
			existingUser.setFullName(user.getFullName());
			existingUser.setPhone(user.getPhone());
			existingUser.setModified(LocalDateTime.now());
			existingUser.setGender(user.getGender());
			existingUser.setJobRole(user.getJobRole());
			return userRepository.save(existingUser);
		}

		throw new UserException("User Not Found");
	}

	@Override
	public String uploadProfileImage(MultipartFile image, Long userId) throws UserException {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
		try {
			String contentType = image.getContentType();
			if (contentType == null
					|| !List.of("image/jpeg", "image/png", "image/webp", "image/gif").contains(contentType)) {
				throw new UserException("Invalid file type. Only images are allowed.");
			}

			if (image.getSize() > 1 * 1024 * 1024) {
				throw new UserException("File size exceeds the maximum limit (1MB).");
			}

			// Save to DB or file system
			byte[] imageData = image.getBytes();
			user.setImage(imageData); // Assuming you're storing image as byte[] in DB
			userRepository.save(user);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store image", e);
		}

		return "Image uploaded successfully!...";
	}

	@Override
	public User getUserByEmailId(String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}

	@Override
	public List<User> getAllRefralById(Long userId) throws UserException {

		List<User> users = userRepository.findUserByReferredBy(userId);

		return users;
	}

}
