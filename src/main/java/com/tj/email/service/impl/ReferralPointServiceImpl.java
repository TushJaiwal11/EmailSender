package com.tj.email.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.User;
import com.tj.email.repository.ReferralPointRepository;
import com.tj.email.repository.UserRepository;
import com.tj.email.service.ReferralPointService;

@Service
public class ReferralPointServiceImpl implements ReferralPointService {

	@Autowired
	private ReferralPointRepository referralPointRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ReferralPoint createCart(Long parentId, User user) {

		ReferralPoint newReferralPoint = new ReferralPoint();

		newReferralPoint.setReferralPoint(0L);
		newReferralPoint.setActiveSubscriptionPoint(0L);
		newReferralPoint.setActiveSubscription(2L);
		newReferralPoint
				.setTotalPoint(newReferralPoint.getActiveSubscriptionPoint() + newReferralPoint.getReferralPoint());
		newReferralPoint.setReferredBy(parentId);
		newReferralPoint.setUser(user);

		return referralPointRepository.save(newReferralPoint);
	}

	@Override
	public ReferralPoint getReferralPointByUserId(Long userId) throws UserException {

		return referralPointRepository.findReferralPointByUserId(userId)
				.orElseThrow(() -> new UserException("Referral point not found for user ID: " + userId));
	}

	@Override
	public ReferralPoint getReferralPointById(Long id) throws UserException {
		return referralPointRepository.findById(id)
				.orElseThrow(() -> new UserException("Referral point not found for user ID: " + id));
	}

	@Override
	public ReferralPoint getReferralPointByreferredBy(Long userId) throws UserException {

		return referralPointRepository.findReferralPointByReferredBy(userId)
				.orElseThrow(() -> new UserException("Referral point not found for user ID: " + userId));
	}

}