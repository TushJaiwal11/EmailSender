package com.tj.email.controller;

import com.tj.email.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.User;
import com.tj.email.service.ReferralPointService;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/")
public class ReferralPointController {

	@Autowired
	private ReferralPointService referralPointService;

	@Autowired
	private UserService userService;

	@GetMapping("/referral/points")
	public ResponseEntity<ReferralPoint> getReferralPointHandler(@RequestHeader("Authorization") String jwt)
			throws UserException {

		UserDto profile = userService.getProfile(jwt);
		User user = userService.getProfileById(profile.getId());
		ReferralPoint referralPoint = referralPointService.getReferralPointById(user.getReferralPoint().getId());

		return new ResponseEntity<ReferralPoint>(referralPoint, HttpStatus.OK);

	}

}
