package com.tj.email.service;

import java.util.List;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;

public interface AdminService {

	public List<User> getAllUsers(User user) throws UserException;

	public List<User> getSubscriptionUsers();

	public String deactivateUser(User user, Long userId) throws UserException;

	public String updateUserData(User user, Long adminId, Long userId) throws UserException;

	public List<User> getAllSubscriptionsUsers();
}
