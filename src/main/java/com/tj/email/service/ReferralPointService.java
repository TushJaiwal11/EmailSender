package com.tj.email.service;

import com.tj.email.exception.UserException;
import com.tj.email.model.ReferralPoint;
import com.tj.email.model.User;

public interface ReferralPointService {

	public ReferralPoint createCart(Long parentId, User user);

	public ReferralPoint getReferralPointByUserId(Long userId) throws UserException;

	public ReferralPoint getReferralPointById(Long id) throws UserException;

	public ReferralPoint getReferralPointByreferredBy(Long userId) throws UserException;

}