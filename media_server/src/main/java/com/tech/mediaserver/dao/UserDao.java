package com.tech.mediaserver.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.User;

@Mapper
public interface UserDao {

	public User loginUser(User user);
	
	public List<User> getUserInfo();
	
	public Integer updateUser(User users);
	
}
