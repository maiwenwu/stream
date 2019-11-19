package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.Satllite;

@Mapper
public interface SatlliteDao {

	public List<Satllite> getAllSatllite();
	
	public Integer deleteSatellite();
	
	public Integer addSatelliteList(@Param("satllites")List<Satllite> satllites);
	
	public List<Satllite> selectAllSatllite(@Param("keyWord")String keyWord);
	
	public Satllite getSatlliteBySatId(Integer id);	
	
	public Integer addSatllite(Satllite satllite);
	
	public List<Satllite> getSatBySatList(List<Integer> satList);
	
	public Integer insertSatlliteList(@Param("satllite_list")List<Satllite> satllite_list);
	
	public Integer updateSatllite(Satllite satllite);
	
	public Integer deleteSatlliteByIds(Integer[] ids);
}
