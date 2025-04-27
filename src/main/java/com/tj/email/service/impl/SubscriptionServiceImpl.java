package com.tj.email.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.Subscription;
import com.tj.email.model.SubscriptionHistory;
import com.tj.email.model.User;
import com.tj.email.model.domain.PlanType;
import com.tj.email.repository.ReferralPointRepository;
import com.tj.email.repository.SubscriptionHistoryRepository;
import com.tj.email.repository.SubscriptionRepository;
import com.tj.email.repository.UserRepository;
import com.tj.email.service.ReferralPointService;
import com.tj.email.service.SubscriptionService;
import com.tj.email.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReferralPointService referralPointService;

	@Autowired
	private ReferralPointRepository referralPointRepository;

	@Autowired
	private SubscriptionHistoryRepository subscriptionHistoryRepository;

	@Override
	public Subscription createSubscription(User user) throws UserException {
		logger.info("Creating subscription for user ID: {}", user.getId());

		Subscription subscription = new Subscription();
		subscription.setPlanType(PlanType.FREE);
		subscription.setIsValid(false);
		subscription.setUser(user);

		Subscription savedSubscription = subscriptionRepository.save(subscription);
		logger.info("Subscription created: {}", savedSubscription);
		return savedSubscription;
	}

//	@Override
//	@Scheduled(fixedRate = 3600000) // every 1 minute
//	public String getUsersSubscription() throws UserException {
//		logger.info("⏰ Scheduled Task: Checking all user subscriptions...");
//
//		List<Subscription> subscriptions = subscriptionRepository.findAll();
//
//		if (subscriptions.isEmpty()) {
//			logger.warn("⚠️ No subscriptions found in the system.");
//			throw new UserException("No one has subscriptions.");
//		}
//
//		for (Subscription subscription : subscriptions) {
//
//			// Skip already reset subscriptions (FREE & null expiry)
//			if (subscription.getPlanType() == PlanType.FREE && subscription.getSubscriptionExpiryDate() == null) {
//				continue;
//			}
//
//			// Check if subscription is expired
//			if (!isValid(subscription)) {
//				Long userId = subscription.getUser().getId();
//				logger.info("🔁 Resetting expired subscription for userId: {}", userId);
//
//				// Reset subscription
//				subscription.setPlanType(PlanType.FREE);
//				subscription.setIsValid(false);
//				subscription.setSubscriptionExpiryDate(null);
//				subscription.setSubscriptionPurchaseDate(null);
//				subscriptionRepository.save(subscription);
//
//				// Reset user subscription info
//				User profile = userService.getProfileById(userId);
//				profile.setActiveSubscription(2L); // Assuming 2L = FREE
//				profile.setSubscriptionExpiryDate(null);
//				profile.setSubscriptionPurchaseDate(null);
//				profile.setModified(LocalDateTime.now());
//				userRepository.save(profile);
//			}
//		}
//
//		logger.info("✅ Subscription check completed.");
//		return "Checked all subscriptions";
//	}

	@Override
	@Transactional
	public Subscription upgradeSubscription(Long userId, PlanType planType) throws UserException {
		logger.info("Starting subscription upgrade for userId: {}, planType: {}", userId, planType);

		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if (subscription != null) {

			LocalDateTime lastUpgradeTime = subscription.getLastUpgradeTime();
			LocalDateTime currentDateTime = LocalDateTime.now();

			if (lastUpgradeTime != null && lastUpgradeTime.plusMinutes(2).isAfter(currentDateTime)) {
				logger.warn("Upgrade attempt too soon for userId: {}", userId);
				throw new UserException("You must wait 3 minute before upgrading your subscription again.");
			}

			subscription.setPlanType(planType);
			subscription.setIsValid(true);
			subscription.setSubscriptionPurchaseDate(currentDateTime);
			subscription.setLastUpgradeTime(currentDateTime);

			LocalDateTime endDateTime = subscription.getSubscriptionExpiryDate();

			if (planType.equals(PlanType.QUATARLY)) {
				if (endDateTime == null || endDateTime.isBefore(currentDateTime)) {
					subscription.setSubscriptionExpiryDate(currentDateTime.plusMonths(3));
				} else {
					subscription.setSubscriptionExpiryDate(endDateTime.plusMonths(3));
				}
			} else {
				if (endDateTime == null || endDateTime.isBefore(currentDateTime)) {
					subscription.setSubscriptionExpiryDate(currentDateTime.plusMonths(1));
				} else {
					subscription.setSubscriptionExpiryDate(endDateTime.plusMonths(1));
				}
			}

			User profile = userService.getProfileById(userId);
			if (profile == null) {
				logger.error("User not found during upgrade process. ID: {}", userId);
				throw new UserException("User not found for user ID: " + userId);
			}

			profile.setActiveSubscription(1L);
			profile.setPlanType(planType);
			profile.setModified(currentDateTime);
			profile.setSubscriptionExpiryDate(subscription.getSubscriptionExpiryDate());
			profile.setSubscriptionPurchaseDate(subscription.getSubscriptionPurchaseDate());

			userRepository.save(profile);

			if (profile.getReferredBy() != null) {
				ReferralPoint addReferralPoint = referralPointService.getReferralPointByUserId(profile.getReferredBy());

				if (addReferralPoint != null) {
					if (subscription.getSubscriptionCount() == null) {
						subscription.setSubscriptionCount(0L);
					} else {
						subscription.setSubscriptionCount(subscription.getSubscriptionCount() + 1);
					}

					if (subscription.getSubscriptionCount() < 3) {
						long pointsToAdd = switch (subscription.getSubscriptionCount().intValue()) {
						case 0 -> 20L;
						case 1 -> 10L;
						case 2 -> 5L;
						default -> 0L;
						};

						addReferralPoint.setActiveSubscriptionPoint(
								addReferralPoint.getActiveSubscriptionPoint() + pointsToAdd);
					}

					addReferralPoint.setTotalPoint(
							addReferralPoint.getActiveSubscriptionPoint() + addReferralPoint.getReferralPoint());

					referralPointRepository.save(addReferralPoint);
				}
			}

			ReferralPoint removeReferralPoint = referralPointService.getReferralPointByUserId(userId);
			removeReferralPoint.setActiveSubscription(1L);

			SubscriptionHistory subscriptionHistory = new SubscriptionHistory();
			subscriptionHistory.setPlanType(planType);
			subscriptionHistory.setSubscriptionPurchaseDate(currentDateTime);
			subscriptionHistory.setSubscriptionExpiryDate(subscription.getSubscriptionExpiryDate());
			subscriptionHistory.setUser(profile);

			if (removeReferralPoint != null && removeReferralPoint.getTotalPoint() != null) {
				Long point = removeReferralPoint.getTotalPoint();
				if (point > 100) {
					subscriptionHistory.setAmount(0L);
				} else {
					subscriptionHistory.setAmount(100 - point);
				}
			}

			if (removeReferralPoint != null && removeReferralPoint.getTotalPoint() != null) {
				long totalPoint = removeReferralPoint.getTotalPoint();

				if (totalPoint > 100) {
					removeReferralPoint.setTotalPoint(totalPoint - 100);
				} else {
					removeReferralPoint.setReferralPoint(0L);
					removeReferralPoint.setActiveSubscriptionPoint(0L);
					removeReferralPoint.setTotalPoint(0L);
				}

				referralPointRepository.save(removeReferralPoint);
			}

			subscriptionHistoryRepository.save(subscriptionHistory);

			Subscription updatedSubscription = subscriptionRepository.save(subscription);

			logger.info("Subscription upgraded successfully for userId: {}", userId);
			return updatedSubscription;
		}

		logger.error("Subscription not found for userId: {}", userId);
		throw new UserException("Subscription not found for user ID: " + userId);
	}

	@Override
	public boolean isValid(Subscription subscription) throws UserException {
		if (subscription.getPlanType().equals(PlanType.FREE)) {
			return true;
		}
		LocalDateTime endDateTime = subscription.getSubscriptionExpiryDate();
		LocalDateTime currentDateTime = LocalDateTime.now();
		return (endDateTime.isAfter(currentDateTime) || endDateTime.isEqual(currentDateTime));
	}

	@Override
	public List<SubscriptionHistory> findAllSubscriptionHistories(Long userId) {
		logger.info("Fetching subscription history for userId: {}", userId);
		return subscriptionHistoryRepository.findAllByUserIdOrderBySubscriptionPurchaseDateDesc(userId);
	}
}
