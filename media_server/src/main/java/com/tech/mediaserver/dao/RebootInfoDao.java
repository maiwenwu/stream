package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.RebootInfo;

@Mapper
public interface RebootInfoDao {
	
	public List<RebootInfo> getRebootInfo();
	
	public Integer deleteRebootInfo();
	
	public Integer addRebootInfo(@Param("rebootInfos")List<RebootInfo> rebootInfos);
	
}
