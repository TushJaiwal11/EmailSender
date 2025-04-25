package com.tj.email.controller;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tj.email.config.JwtProvider;
import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.User;
import com.tj.email.model.domain.UserRole;
import com.tj.email.repository.ReferralPointRepository;
import com.tj.email.repository.UserRepository;
import com.tj.email.response.UserToken;
import com.tj.email.service.CustomUserServiceImpl;
import com.tj.email.service.ReferralPointService;
import com.tj.email.service.SubscriptionService;

@RestController
@RequestMapping("/auth/")
public class AuthController {

	@Autowired
	private CustomUserServiceImpl customUserServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReferralPointService referralPointService;

	@Autowired
	private ReferralPointRepository referralPointRepository;

	@Autowired
	private SubscriptionService subsciptionService;

	@PostMapping("/signup")
	public ResponseEntity<?> registerUserHandler(@RequestBody User user) {
		
		System.out.println("AuthController.registerUserHandler()");
		try {
			String email = user.getEmail();
			String password = user.getPassword();
			String fullName = user.getFullName();
			String phone = user.getPhone();
			Long rec_status = 1L;
			Long activeSubscription = 2L;

			String referredBy = user.getReferralCode();
			if (referredBy.length() < 3) {
				referredBy = null;
			}

			User existingUser = userRepository.findByEmail(email);
			if (existingUser != null) {
				throw new Exception("User is already registered with email: " + email);
			}

			if (fullName == null || fullName.length() < 2) {
				throw new UserException("Name should be at least 2 characters long");
			}

			int length = fullName.length();
			int mid = length / 2;
			String prefix = fullName.substring(0, mid);

			int randomNum = new Random().nextInt(900) + 100; // 100–999

			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setFullName(fullName);
			newUser.setPhone(phone);
			newUser.setRole(UserRole.ROLE_CUSTOMER);
			newUser.setCreated(LocalDateTime.now());
			newUser.setPasswordValidity(LocalDateTime.now().plusYears(1));
			newUser.setActiveSubscription(activeSubscription);
			newUser.setModified(LocalDateTime.now());
			newUser.setRec_status(rec_status);

			if (referredBy != null) {
				User parentUser = userRepository.findByReferralCode(referredBy);

				if (parentUser != null) {
					newUser.setReferredBy(parentUser.getId());

				} else {
					throw new UserException("Referral code not exist");
				}
			}
			newUser.setReferralCode(prefix + randomNum);
			User createdUser = userRepository.save(newUser);

			subsciptionService.createSubscription(createdUser);

			if (createdUser.getReferredBy() == null) {
				referralPointService.createCart(0L, createdUser);
			} else {
				ReferralPoint cart = referralPointService.createCart(createdUser.getReferredBy(), createdUser);

				ReferralPoint rp = referralPointService.getReferralPointByUserId(createdUser.getReferredBy());

				rp.setReferralPoint(rp.getReferralPoint() + 5L);
				rp.setTotalPoint(rp.getActiveSubscriptionPoint() + rp.getReferralPoint());

				referralPointRepository.save(rp);

			}

			Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = JwtProvider.generateJwtToken(authentication);

			UserToken userResponse = new UserToken();
			userResponse.setJwt(jwt);
			userResponse.setMessage("User has been registered successfully");
			userResponse.setCreateAt(LocalDateTime.now());
			userResponse.setExpiresAt(LocalDateTime.now().plusDays(1));
			userResponse.setSuccess(Boolean.TRUE);

			return new ResponseEntity<>(userResponse, HttpStatus.CREATED);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> loginUserHandler(@RequestBody User user) throws Exception {

		try {
			String email = user.getEmail();
			String password = user.getPassword();

			Authentication authentication = authenticate(email, password);

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = JwtProvider.generateJwtToken(authentication);

			User user1 = userRepository.findByEmail(email);

			user1.setLastLogin(LocalDateTime.now());
			userRepository.save(user1);

			UserToken userResponse = new UserToken();
			userResponse.setJwt(jwt);
			userResponse.setCreateAt(LocalDateTime.now());
			userResponse.setMessage("User Has been logged in");
			userResponse.setExpiresAt(LocalDateTime.now().plusDays(1));
			userResponse.setSuccess(Boolean.TRUE);

			return new ResponseEntity<>(userResponse, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	private Authentication authenticate(String email, String password) {

		UserDetails userDetails = customUserServiceImpl.loadUserByUsername(email);

		if (userDetails == null) {
			System.out.println("Sign-in user Detail is null");
			throw new BadCredentialsException("Invalid username or password");
		}

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			System.out.println("Sign-in User Details -password is not matches :" + userDetails);
			throw new BadCredentialsException("Invalid password");
		}

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

}
