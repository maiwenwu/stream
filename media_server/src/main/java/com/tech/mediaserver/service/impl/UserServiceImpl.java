package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.mediaserver.dao.UserDao;
import com.tech.mediaserver.entity.User;
import com.tech.mediaserver.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;

	@Override
	public User loginUser(User user) {
		return userDao.loginUser(user);
	}

	@Override
	public List<User> getUserInfo() {
		return userDao.getUserInfo();
	}

	@Override
	public Integer updateUser(User users) {
		return userDao.updateUser(users);
	}

	
}
