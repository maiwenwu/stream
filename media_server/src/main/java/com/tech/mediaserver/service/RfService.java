package com.tech.mediaserver.service;

import java.util.List;

import com.tech.mediaserver.entity.Rf;

public interface RfService {

	public List<Rf> getAllRf();
	
	public Integer deleteRf();
	
	public Integer addRf(List<Rf> rfs);
	
	public Rf getRfByBoardId(Integer board_id);
	
	public Integer saveRfInfo(Rf rf);
}
