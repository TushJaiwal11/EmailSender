package com.tj.email.model;

import java.time.LocalDateTime;

import com.tj.email.model.domain.PlanType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime subscriptionPurchaseDate;
	private LocalDateTime subscriptionExpiryDate;

	private PlanType planType;

	private Boolean isValid;

	@OneToOne
	private User user;

	private Long subscriptionCount;

	private LocalDateTime lastUpgradeTime;

}
