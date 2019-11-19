package com.tech.mediaserver.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.Programs;

public interface ProgramsService {

	public List<Programs> getAllPrograms();
	
	public List<Programs> getProgramsByTpId(Integer tp_id,Integer board_id);
	
	public List<Programs> getSearchProgramsByTpId(Integer tp_id, Integer board_id);
	
	public Integer deleteProByTpListAndBoardId(List<Integer> aList,Integer board_id);
	
	public Integer deleteProByTpId(Integer tp_id);
	
	public List<Integer> getTpIdList(Integer board_id, Integer module_id);
	
	public Programs getProById(Integer pro_id,Integer board_id);
	
	public List<Programs> getProByTpList(List<Integer> tp_list,Integer board_id);
	
	public List<Programs> getProByBoardId(Integer board_id);
	
	public Integer deleteProByBoardId(Integer board_id);
	
	public PageInfo<Programs> selectAllPro(Integer page, Integer size, String keyWord, List<Integer> tp_list, Integer board_id);
	
	public Integer updateProgram(Programs programs);
	
	public Integer deleteProgramByIds(Integer[] ids);
	
	public Integer deleteSearchPrograms();
	
	public Integer deletePrograms();
	
	public Integer addPrograms(List<Programs> programs);

}


