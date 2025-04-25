package com.tj.email.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/profile")
	public ResponseEntity<User> getProfileHandler(@RequestHeader("Authorization") String jwt) throws UserException {

		User profile = userService.getProfile(jwt);

		return new ResponseEntity<>(profile, HttpStatus.OK);
	}

	@PatchMapping("/profile/update/{userId}")
	public ResponseEntity<User> updateProfileHandler(@RequestHeader("Authorization") String jwt,
			@PathVariable Long userId, @RequestBody User user) throws UserException {

		User profile = userService.getProfile(jwt);

		if (profile == null) {
			throw new UserException("User not found or else please re login");
		}

		User updatedUser = userService.updateUser(user, userId);

		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@PatchMapping("/profile/upload-image")
	public ResponseEntity<String> updateProfileImageHandler(@RequestHeader("Authorization") String jwt,
			@RequestParam("image") MultipartFile image) throws UserException {

		User profile = userService.getProfile(jwt);

		if (profile == null) {
			throw new UserException("User not found or else please re login");
		}

		String message = userService.uploadProfileImage(image, profile.getId());

		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsersHandler(@RequestHeader("Authorization") String jwt)
			throws UserException {

		User profile = userService.getProfile(jwt);

		List<User> allUser = userService.getAllUser(profile.getId());

		return new ResponseEntity<>(allUser, HttpStatus.OK);
	}

	@GetMapping("referral/referred-users")
	public ResponseEntity<List<User>> getreferredUsersHandler(@RequestHeader("Authorization") String jwt)
			throws UserException {

		User profile = userService.getProfile(jwt);

		List<User> users = userService.getAllRefralById(profile.getId());

		return new ResponseEntity<>(users, HttpStatus.OK);
	}

}
