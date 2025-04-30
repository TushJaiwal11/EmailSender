package com.tj.email.controller;

import java.util.Base64;
import java.util.List;

import com.tj.email.model.PDF;
import com.tj.email.model.dto.PDFDTO;
import com.tj.email.service.PDFService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.service.AdminService;
import com.tj.email.service.UserService;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/admin/")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserService userService;

	@Autowired
	private PDFService pdfService;

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUserByNewestUser(@RequestHeader("Authorization") String jwt)
			throws UserException {
		logger.info("GET /users - Fetching all users (sorted by newest) for JWT: {}", jwt);
		User profile = userService.getProfile(jwt);
		logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
		List<User> allUsers = adminService.getAllUsers(profile);
		logger.info("Successfully fetched {} users", allUsers.size());
		return ResponseEntity.ok(allUsers);
	}

	@PatchMapping("/deactivate/user/{userId}")
	public ResponseEntity<String> deactivateUser(@RequestHeader("Authorization") String jwt, @PathVariable Long userId)
			throws UserException {
		logger.info("PATCH /deactivate/user/{} - Deactivating user with JWT: {}", userId, jwt);
		User profile = userService.getProfile(jwt);
		logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
		String result = adminService.deactivateUser(profile, userId);
		logger.info("User {} deactivated successfully", userId);
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/update/user/{userId}")
	public ResponseEntity<String> updateUserData(@RequestHeader("Authorization") String jwt, @RequestBody User user,
												 @PathVariable Long userId) throws UserException {
		logger.info("PATCH /update/user/{} - Updating user with JWT: {}", userId, jwt);
		User profile = userService.getProfile(jwt);
		logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
		String result = adminService.updateUserData(user, profile.getId(), userId);
		logger.info("User {} updated successfully", userId);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/subscription-users")
	public ResponseEntity<List<User>> getAllSubscriptionUsers(@RequestHeader("Authorization") String jwt)
			throws UserException {
		logger.info("GET /subscription-users - Fetching all subscription users for JWT: {}", jwt);
		User profile = userService.getProfile(jwt);
		logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
		List<User> subscriptionsUsers = adminService.getAllSubscriptionsUsers();
		logger.info("Fetched {} subscribed users", subscriptionsUsers.size());
		return ResponseEntity.ok(subscriptionsUsers);
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadPdf(
			@RequestBody PDFDTO pdfdto,
			@RequestHeader("Authorization") String jwt
	) {
		logger.info("POST /upload - Uploading PDF with title: {}", pdfdto.getTitle());
		try {
			User profile = userService.getProfile(jwt);
			logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
			PDF savedPdf = pdfService.savePdf(profile, pdfdto);
			logger.info("PDF uploaded successfully with ID: {}", savedPdf.getId());
			return ResponseEntity.ok("PDF uploaded successfully with ID: " + savedPdf.getId());
		} catch (Exception e) {
			logger.error("Error uploading PDF: {}", e.getMessage(), e);
			return ResponseEntity.status(500).body("Error uploading PDF: " + e.getMessage());
		}
	}

	@GetMapping("/getAllPdf")
	public ResponseEntity<List<PDFDTO>> getAllPdfs(@RequestHeader("Authorization") String jwt)
			throws UserException {
		logger.info("GET /getAllPdf - Fetching all PDFs for JWT: {}", jwt);
		User profile = userService.getProfile(jwt);
		logger.debug("Authenticated user: {} with role: {}", profile.getEmail(), profile.getRole());
		List<PDFDTO> allPdf = pdfService.getAllPdf();
		logger.info("Successfully fetched {} PDFs", allPdf.size());
		return new ResponseEntity<>(allPdf, HttpStatus.OK);
	}
}
