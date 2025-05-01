package com.tj.email.controller;

import java.util.List;

import com.tj.email.model.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tj.email.exception.UserException;
import com.tj.email.model.Subscription;
import com.tj.email.model.SubscriptionHistory;
import com.tj.email.model.User;
import com.tj.email.model.domain.PlanType;
import com.tj.email.service.SubscriptionService;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/")
public class SubscriptionController {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private UserService userService;

//	@GetMapping("/user/subscription")
//	public ResponseEntity<String> getUserSubscription(@RequestHeader("Authorization") String jwt) throws UserException {
//		logger.info("GET /user/subscription - Received request to check subscription");
//
//		User profile = userService.getProfile(jwt);
//		logger.debug("Fetched user profile for ID: {}", profile.getId());
//
//		String usersSubscription = subscriptionService.getUsersSubscription();
//
//		logger.info("Subscription check completed for userId: {}", profile.getId());
//		return new ResponseEntity<>(usersSubscription, HttpStatus.OK);
//	}

	@PutMapping("/upgrade-plan")
	public ResponseEntity<Subscription> upgradeSubscription(@RequestHeader("Authorization") String jwt,
			@RequestParam("planType") PlanType planType) throws UserException {

		logger.info("PUT /upgrade-plan - Request to upgrade subscription to: {}", planType);

		UserDto profile = userService.getProfile(jwt);
		if (profile == null) {
			logger.error("User profile not found from JWT token.");
			throw new UserException("User not found for JWT token");
		}

		logger.debug("User profile retrieved successfully. ID: {}", profile.getId());

		Subscription usersSubscription = subscriptionService.upgradeSubscription(profile.getId(), planType);

		logger.info("Subscription upgrade successful for userId: {}", profile.getId());
		return new ResponseEntity<>(usersSubscription, HttpStatus.OK);
	}

	@GetMapping("/user/subscription/history")
	public ResponseEntity<List<SubscriptionHistory>> getUserSubscriptionHistory(
			@RequestHeader("Authorization") String jwt) throws UserException {

		logger.info("GET /user/subscription/history - Fetching subscription history");

		UserDto profile = userService.getProfile(jwt);
		if (profile == null) {
			logger.error("User profile not found for JWT token.");
			throw new UserException("User Not Found");
		}

		List<SubscriptionHistory> subscriptionHistories = subscriptionService
				.findAllSubscriptionHistories(profile.getId());

		logger.info("Subscription history retrieved successfully for userId: {}", profile.getId());
		return new ResponseEntity<>(subscriptionHistories, HttpStatus.OK);
	}
}
