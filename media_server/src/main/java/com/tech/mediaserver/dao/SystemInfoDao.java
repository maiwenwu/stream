package com.tech.mediaserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.SystemInfo;

@Mapper
public interface SystemInfoDao {

	public SystemInfo getLogLevel();
	
	public Integer setLogLevel(@Param("log_level") Integer log_level);
	
}
