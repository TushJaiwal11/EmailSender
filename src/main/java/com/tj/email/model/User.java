package com.tj.email.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tj.email.model.domain.PlanType;
import com.tj.email.model.domain.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "user_master")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	@Lob
	@Column(name = "image", columnDefinition = "LONGBLOB")
	private byte[] image;
	private String fullName;
	private String phone;
	private UserRole role;

	private LocalDateTime created;
	private LocalDateTime modified;
	private LocalDateTime lastLogin;
	private Long rec_status;
	private Long activeSubscription;
	private LocalDateTime passwordValidity;
	private LocalDateTime subscriptionPurchaseDate;
	private LocalDateTime subscriptionExpiryDate;
	private String gender;
	private String referralCode;
	private String jobRole;
	private Long referredBy;

	private PlanType planType;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EmailConfig> emailConfig = new ArrayList<>();

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReferralPoint referralPoint;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SubscriptionHistory> subscriptionHistories = new ArrayList<>();

}
