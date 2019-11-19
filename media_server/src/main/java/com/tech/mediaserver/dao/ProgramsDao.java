package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.Programs;

@Mapper
public interface ProgramsDao {

	public List<Programs> getAllPrograms();
	
	public List<Programs> getProgramsByTpId(@Param("tp_id")Integer tp_id,@Param("board_id") Integer board_id);
	
	public List<Programs> getSearchProgramsByTpId(@Param("tp_id")Integer tp_id,@Param("board_id") Integer board_id);
	
	public Integer deleteProByTpListAndBoardId(@Param("tp_list")List<Integer> tList,@Param("board_id") Integer board_id);
	
	public Integer deleteProByTpId(Integer tp_id);
	
	public List<Integer> getTpIdList(@Param("board_id") Integer board_id, @Param("module_id") Integer module_id);
	
	public Programs getProById(@Param("id")Integer pro_id,@Param("board_id") Integer board_id);
	
	public List<Programs> getProByTpList(@Param("tp_list")List<Integer> tp_list,@Param("board_id")Integer board_id);
	
	public List<Programs> getProByBoardId(@Param("board_id")Integer board_id);
	
	public Integer deleteProByBoardId(@Param("board_id")Integer board_id);

	public List<Programs> selectAllPro(@Param("tp_list")List<Integer> tp_list, @Param("keyWord")String keyWord, @Param("board_id")Integer board_id);
	
	public Integer updateProgram(Programs programs);
	
	public Integer deleteProgramByIds(Integer[] ids);
	
	public Integer deleteSearchPrograms();
	
	public Integer deletePrograms();
	
	public Integer addPrograms(@Param("programs")List<Programs> programs);

	
}
