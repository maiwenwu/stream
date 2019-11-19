package com.tech.mediaserver.service;

import java.util.List;

import com.tech.mediaserver.entity.RebootInfo;
import com.tech.mediaserver.entity.SystemInfo;

public interface SystemInfoService {

	public SystemInfo getLogLevel();
	
	public Integer setLogLevel(Integer log_level);
	
	public List<RebootInfo> getRebootInfo();
	
	public Integer deleteRebootInfo();
	
	public Integer addRebootInfo(List<RebootInfo> rebootInfos);
	
}
