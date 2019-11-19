package com.tech.mediaserver.service;

import java.util.List;

import com.tech.mediaserver.entity.User;

public interface UserService {

	public User loginUser(User user);
	
	public List<User> getUserInfo();
	
	public Integer updateUser(User users);
	
}
