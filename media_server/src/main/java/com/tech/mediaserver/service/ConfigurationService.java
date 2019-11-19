package com.tech.mediaserver.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface ConfigurationService {

	public void exportExcel(HttpServletResponse response);
	
	public int importExcel(MultipartFile file);
	
	public void deleteChannelRecord(); 
	
	public boolean uploadFile(MultipartFile file);
	
	public JSONObject readLog(Integer type, Integer file_pos, Integer log_level);
	
	public Long getFileSize(Integer type, Integer log_level);
	
	public JSONArray getChangeLog();
	
}
