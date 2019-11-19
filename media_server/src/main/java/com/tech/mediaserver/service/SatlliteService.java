package com.tech.mediaserver.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.Satllite;

public interface SatlliteService {

	public List<Satllite> getAllSatllite();
	
	public Integer deleteSatellite();
	
	public Integer addSatelliteList(List<Satllite> satllites);
	
	public Satllite getSatlliteBySatId(Integer id);
	
	public Integer addSatllite(Satllite satllite);
	
	public List<Satllite> getSatBySatList(List<Integer> satList);
	
	public PageInfo<Satllite> selectAll(Integer page, Integer size, String keyWord);
	
	public List<Satllite> insertSatlliteList(MultipartFile file);
	
	public Integer updateSatllite(Satllite satllite);
	
	public Integer deleteSatlliteByIds(Integer[] ids);
	
	public Integer checkSatToDelete(Integer[] ids);
	
	public Integer deleteSatllite(Integer[] ids);
}
