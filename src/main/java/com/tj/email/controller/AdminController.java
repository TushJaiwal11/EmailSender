package com.tj.email.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.service.AdminService;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUserByNewestUser(@RequestHeader("Authorization") String jwt)
			throws UserException {

		logger.info("Received request to fetch all users from newest with JWT: {}", jwt);

		User profile = userService.getProfile(jwt);
		logger.info("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());

		List<User> allUsers = adminService.getAllUsers(profile);
		logger.info("Fetched {} users for adminService", allUsers.size());

		return ResponseEntity.ok(allUsers);
	}

	@PatchMapping("/deactivate/user/{userId}")
	public ResponseEntity<String> deactivateUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId)
			throws UserException {

		logger.info("Received request to fetch all users from newest with JWT: {}", jwt);

		User profile = userService.getProfile(jwt);
		logger.info("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());

		String deactivateUser = adminService.deactivateUser(profile, userId);

		return ResponseEntity.ok(deactivateUser);
	}

	@PatchMapping("/update/user/{userId}")
	public ResponseEntity<String> updateUserData(@RequestHeader("Authorization") String jwt, @RequestBody User user,
			@PathVariable Long userId) throws UserException {

		logger.info("Received request to fetch all users from newest with JWT: {}", jwt);

		User profile = userService.getProfile(jwt);
		logger.info("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());

		String deactivateUser = adminService.updateUserData(user, profile.getId(), userId);

		return ResponseEntity.ok(deactivateUser);
	}

	@GetMapping("/subscription-users")
	public ResponseEntity<List<User>> getAllSubscriptionUsers(@RequestHeader("Authorization") String jwt)
			throws UserException {

		logger.info("Received request to fetch all users from newest with JWT: {}", jwt);

		User profile = userService.getProfile(jwt);
		logger.info("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());

		List<User> subscriptionsUsers = adminService.getAllSubscriptionsUsers();
		logger.info("Fetched {} all subscriptions users", subscriptionsUsers.size());

		return ResponseEntity.ok(subscriptionsUsers);
	}

}
