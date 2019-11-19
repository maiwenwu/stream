package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.Transponder;

@Mapper
public interface TpDao {

	public List<Transponder> getTpBySatId(@Param("sat_id")Integer sat_id, @Param("max_tp_id")Integer max_tp_id);
	
	public Transponder getTpByTpId(Integer tp_id);
	
	public Integer getMaxTpId(Integer sat_id);
	
	public List<Integer> getSatIdByTpList(List<Integer> tpList);
	
	public List<Transponder> getTpListWithPro(@Param("sat_id")Integer sat_id , @Param("board_id")Integer board_id);
	
	public List<Transponder> getTpListBySatList(List<Integer> sat_list);
	
	public Integer deleteTpByIdList(List<Integer> id_list);
	
	public List<Transponder> selectAllTp(@Param("sat_id")Integer sat_id, @Param("keyWord")String keyWord);
	
	public Integer addTransponder(Transponder transponder);
	
	public Integer updateTransponder(Transponder transponder);
	
	public List<Transponder> getSearchTp();
	
	public Integer deleteSearchTp();
	
	public List<Transponder> getAllTp();
	
	public Integer deleteTp();
	
	public Integer addTp(@Param("transponders")List<Transponder> transponders);
	
	public List<Transponder> getTpByTpInfo(Integer sat_id, Integer freq1, Integer freq2, Integer symbolRate1, Integer symbolRate2, Integer 	polarization);
	
}
