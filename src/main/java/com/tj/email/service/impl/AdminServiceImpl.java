package com.tj.email.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.model.domain.UserRole;
import com.tj.email.repository.SubscriptionRepository;
import com.tj.email.repository.UserRepository;
import com.tj.email.service.AdminService;
import com.tj.email.service.SubscriptionService;
import com.tj.email.service.UserService;

@Service
public class AdminServiceImpl implements AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Override
	public List<User> getAllUsers(User user) throws UserException {

		logger.info("Fetching all users for Admin Access: {}", user.getEmail());

		if (user.getRole().equals(UserRole.ROLE_ADMIN)) {
			List<User> all = userRepository.findAllByOrderByCreatedDesc();
			logger.info("Found {} users sorted by created date", all.size());
			return all;
		}

		logger.warn("Unauthorized access attempt by user: {}", user.getEmail());
		throw new UserException("Only admin can have access");
	}

	@Override
	public List<User> getSubscriptionUsers() {
		logger.debug("getSubscriptionUsers called");
		return null;
	}

	@Override
	public String deactivateUser(User user, Long userId) throws UserException {

		User profile = userService.getProfileById(userId);

		if (user.getRole().equals(UserRole.ROLE_ADMIN) && profile != null) {
			profile.setRec_status(2L);
			User save = userRepository.save(profile);

			if (save.getRec_status() == 2) {
				return "User Has been Deactivate";
			}

		}
		throw new UserException("User still Ativate....");
	}

	@Override
	public String updateUserData(User user, Long adminId, Long userId) throws UserException {

		User admin = userService.getProfileById(adminId);

		if (!admin.getRole().equals(UserRole.ROLE_ADMIN)) {
			throw new UserException("Admin can direct update the user");
		}

		subscriptionService.upgradeSubscription(userId, user.getPlanType());

		User existingUser = userRepository.findById(userId).orElse(null);

		if (existingUser != null && user.getId().equals(existingUser.getId())) {
			existingUser.setFullName(user.getFullName());
			existingUser.setModified(LocalDateTime.now());
			existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
			existingUser.setPasswordValidity(user.getPasswordValidity());
			logger.info("User {} update the data");

			userRepository.save(existingUser);
		}
		return "User Has been Update";
	}

	@Override
	public List<User> getAllSubscriptionsUsers() {
		return userRepository.findByActiveSubscriptionOrderBySubscriptionPurchaseDateDesc(1L);
	}

}
