package com.tj.email.service;

import java.util.List;

import com.tj.email.exception.UserException;
import com.tj.email.model.Subscription;
import com.tj.email.model.SubscriptionHistory;
import com.tj.email.model.User;
import com.tj.email.model.domain.PlanType;

public interface SubscriptionService {

	Subscription createSubscription(User user) throws UserException;

	String getUsersSubscription() throws UserException;

	Subscription upgradeSubscription(Long userId, PlanType planType) throws UserException;

	boolean isValid(Subscription subscription) throws UserException;

	List<SubscriptionHistory> findAllSubscriptionHistories(Long userId);
}
