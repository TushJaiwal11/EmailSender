package com.tj.email.service;

import java.util.List;

import com.tj.email.model.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import com.tj.email.exception.UserException;
import com.tj.email.model.User;

public interface UserService {

	public UserDto getProfile(String jwt) throws UserException;

	public User getUserByEmailId(String email);

	public User getProfileById(Long userId) throws UserException;

	public List<User> getAllUser(Long userId) throws UserException;

	public User updateUser(User user, Long userid) throws UserException;

	public String uploadProfileImage(MultipartFile image, Long userId) throws UserException;

	public List<User> getAllRefralById(Long userId) throws UserException;
}
