
package com.tj.email.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tj.email.model.ReferralPoint;

public interface ReferralPointRepository extends JpaRepository<ReferralPoint, Long> {

	Optional<ReferralPoint> findReferralPointByUserId(Long userId);

	Optional<ReferralPoint> findReferralPointByReferredBy(Long userId);

}
