package com.tj.email.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tj.email.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	public Subscription findByUserId(Long userId);

	List<Subscription> findByIsValidTrueOrderBySubscriptionExpiryDateAsc();

}
