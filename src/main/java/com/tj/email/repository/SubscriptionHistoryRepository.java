package com.tj.email.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tj.email.model.SubscriptionHistory;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Long> {

	List<SubscriptionHistory> findAllByUserIdOrderBySubscriptionPurchaseDateDesc(Long userId);

}
