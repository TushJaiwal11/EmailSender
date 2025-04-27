package com.tj.email.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.User;
import com.tj.email.model.domain.PlanType;
import com.tj.email.response.PaymentLinkResponse;
import com.tj.email.service.ReferralPointService;
import com.tj.email.service.UserService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String secretKey;

	private final ReferralPointService referralPointService;

	private final UserService userService;

	public PaymentController(ReferralPointService referralPointService, UserService userService) {
		this.referralPointService = referralPointService;
		this.userService = userService;
	}

	@PostMapping("/{planType}")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable PlanType planType,
			@RequestHeader("Authorization") String jwt) throws UserException, RazorpayException {

		User profile = userService.getProfile(jwt);
		ReferralPoint referralPoint = referralPointService.getReferralPointByUserId(profile.getId());

		long baseAmount = 100L * 100L; // ₹100 in paise
		long amount = baseAmount;

		if (planType == PlanType.QUATARLY) {
			amount = (long) (baseAmount * 2.5); // ₹250
		}

		if (referralPoint.getTotalPoint() != null) {
			long points = referralPoint.getTotalPoint();
			long discount = points * 100; // 1 point = ₹1 = 100 paise
			amount = Math.max(0L, amount - discount);
		}

		RazorpayClient razorpayClient = new RazorpayClient(apiKey, secretKey);

		JSONObject paymentLinkRequest = new JSONObject();
		paymentLinkRequest.put("amount", amount);
		paymentLinkRequest.put("currency", "INR");

		JSONObject customer = new JSONObject();
		customer.put("name", profile.getFullName());
		customer.put("email", profile.getEmail());
		paymentLinkRequest.put("customer", customer);

		JSONObject notify = new JSONObject();
		notify.put("email", true);
		paymentLinkRequest.put("notify", notify);

		paymentLinkRequest.put("callback_url", "https://get-easy-job.vercel.app/upgrade/success?planType=" + planType);
		paymentLinkRequest.put("callback_method", "get");

		PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);

		PaymentLinkResponse response = new PaymentLinkResponse();
		response.setPayment_link_id(payment.get("id"));
		response.setPayment_link_url(payment.get("short_url"));

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
