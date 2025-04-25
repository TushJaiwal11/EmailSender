package com.tj.email.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tj.email.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmail(String email);

	public User findByReferralCode(String code);

	public List<User> findUserByReferredBy(Long id);

	public List<User> findAllByOrderByCreatedDesc();

	public List<User> findByActiveSubscriptionOrderBySubscriptionPurchaseDateDesc(Long activeSubscription);

}
