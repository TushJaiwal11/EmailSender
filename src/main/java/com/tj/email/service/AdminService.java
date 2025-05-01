package com.tj.email.service;

import java.util.List;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;
import com.tj.email.model.dto.UserDto;

public interface AdminService {

	public List<UserDto> getAllUsers(UserDto user) throws UserException;

	public List<User> getSubscriptionUsers();

	public String deactivateUser(UserDto user, Long userId) throws UserException;

	public String updateUserData(User user, Long adminId, Long userId) throws UserException;

	public List<User> getAllSubscriptionsUsers();
}
