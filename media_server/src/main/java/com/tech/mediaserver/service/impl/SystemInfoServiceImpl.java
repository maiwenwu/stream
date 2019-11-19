package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.mediaserver.dao.RebootInfoDao;
import com.tech.mediaserver.dao.SystemInfoDao;
import com.tech.mediaserver.entity.RebootInfo;
import com.tech.mediaserver.entity.SystemInfo;
import com.tech.mediaserver.service.SystemInfoService;

@Service
@Transactional
public class SystemInfoServiceImpl implements SystemInfoService{

	@Autowired
	private SystemInfoDao systemInfoDao;
	@Autowired
	private RebootInfoDao rebootInfoDao;
	
	@Override
	public SystemInfo getLogLevel() {
		return systemInfoDao.getLogLevel();
	}

	@Override
	public Integer setLogLevel(Integer log_level) {
		return systemInfoDao.setLogLevel(log_level);
	}

	@Override
	public List<RebootInfo> getRebootInfo() {
		return rebootInfoDao.getRebootInfo();
	}

	@Override
	public Integer deleteRebootInfo() {
		return rebootInfoDao.deleteRebootInfo();
	}

	@Override
	public Integer addRebootInfo(List<RebootInfo> rebootInfos) {
		return rebootInfoDao.addRebootInfo(rebootInfos);
	}

}
