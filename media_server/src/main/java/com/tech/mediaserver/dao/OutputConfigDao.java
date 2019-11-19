package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.OutputConfig;

@Mapper
public interface OutputConfigDao {

	public List<OutputConfig> getOutputById(@Param("board_id")Integer board_id,@Param("module_id")Integer module_id);
	
	public Integer addOutputConfig(OutputConfig outputConfigs);
	
	public OutputConfig getOutput(@Param("id") Integer id);
	
	public List<OutputConfig> getOutputBySatId(@Param("board_id") Integer board_id);
	
	public Integer updateOutput(OutputConfig outputConfig);
	
	public Integer deleteOutput(@Param("id") Integer id);
	
	public Integer deleteAllStreamingByBoardId(@Param("board_id") Integer board_id);
	
	public List<OutputConfig> getAllOutPut();
	
	public List<OutputConfig> getOutputBySatIdList(List<Integer> sat_id_list);
	
	public List<OutputConfig> getOutputByTpIdList(List<Integer> tp_id_list);
	
	public List<OutputConfig> getOutputByProIdList(List<Integer> pro_id_list);
	
	public List<OutputConfig> getOutputByBoardIdAndSatIdAndTpId(@Param("board_id")Integer board_id, @Param("sat_id")Integer sat_id, @Param("tp_id")Integer tp_id);
	
	public Integer deleteOutputByIdList(List<Integer> id_list);
	
	public List<OutputConfig> getOutputByIdList(List<Integer> id_list);
	
	public Integer deleteAllOutPut();
	
	public Integer addOutput(@Param("outputConfigs")List<OutputConfig> outputConfigs);
}
