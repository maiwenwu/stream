package com.tech.mediaserver.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.Transponder;

public interface TpService {

	public List<Transponder> getTpBySatId(Integer sat_id,Integer max_tp_id);
	
	public Transponder getTpByTpId(Integer tp_id);
	
	public Integer getMaxTpId(Integer sat_id);
	
	public List<Integer> getSatIdByTpList(List<Integer> tpList);
	
	public List<Transponder> getTpListWithPro(Integer sat_id, Integer board_id);
	
	public List<Transponder> getTpListBySatList(List<Integer> sat_list);
	
	public Integer deleteTpByIdList(List<Integer> id_list);
	
	public PageInfo<Transponder> selectAll(Integer page, Integer size, String keyWord, Integer sat_id);
	
	public Integer addTransponder(Transponder transponder);
	
	public Integer updateTransponder(Transponder transponder);
	
	public List<Transponder> getSearchTp();
	
	public Integer deleteSearchTp();
	
	public List<Transponder> getAllTp();
	
	public Integer deleteTp();
	
	public Integer addTp(List<Transponder> transponders);
	
	public Integer deleteTransponder(Integer[] ids);
	
	public Integer checkTpToDelete(Integer[] ids);
	
	public List<Transponder> getTpByTpInfo(Transponder transponder);
	
}
