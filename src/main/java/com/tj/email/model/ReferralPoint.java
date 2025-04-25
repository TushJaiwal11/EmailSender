package com.tj.email.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ReferralPoint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long referredBy; // who refer

	private Long referralPoint; // if u are refer some one and he login then you can credit 5 point

	private Long activeSubscriptionPoint; // if u are refer some one and he purchesed activeSubscription then who refer
											// to him he get credit more 20 point

	private Long totalPoint; // after calculation referralPoint+activeSubscriptionPoint+ownSubscriptionPoint
								// u get 125 points referredBy user

	private Long activeSubscription; // if he is actively using subscription if yes 1,not 2

	@OneToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;

}