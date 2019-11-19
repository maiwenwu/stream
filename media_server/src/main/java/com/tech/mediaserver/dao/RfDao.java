package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.Rf;

@Mapper
public interface RfDao {

	public List<Rf> getAllRf();
	
	public Integer deleteRf();
	
	public Integer addRf(@Param("rfs")List<Rf> rfs);
	
	public Rf getRfByBoardId(Integer board_id);
	
	public Integer saveRfInfo(Rf rf);
	
}
