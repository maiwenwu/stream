package com.tech.mediaserver.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.OutputConfig;

public interface OutputConfigService {

	public List<OutputConfig> getOutputById(Integer board_id, Integer module_id);
	
	public Integer addOutputConfig(OutputConfig outputConfigs);
	
	public OutputConfig getOutput(Integer id);
	
	public List<OutputConfig> getOutputByBoardId( Integer board_id);
	
	public Integer updateOutput(OutputConfig outputConfig);
	
	public Integer deleteOutput(Integer id);
	
	public Integer deleteAllStreamingByBoardId(Integer board_id);
	
	public List<OutputConfig> getAllOutPut();
	
	public List<OutputConfig> getOutputBySatIdList(List<Integer> sat_id_list);
	
	public List<OutputConfig> getOutputByTpIdList(List<Integer> tp_id_list);
	
	public List<OutputConfig> getOutputByProIdList(List<Integer> pro_id_list);
	
	public List<OutputConfig> getOutputByBoardIdAndSatIdAndTpId(@Param("board_id")Integer board_id, @Param("sat_id")Integer sat_id, @Param("tp_id")Integer tp_id);
	
	public Integer deleteOutputByIdList(List<Integer> id_list);
	
	public List<OutputConfig> getOutputByIdList(List<Integer> id_list);
	
	public PageInfo<OutputConfig> selectAllStreams(Integer board_id, Integer module_id, Integer page, Integer size);
	
	public Integer deleteAllOutPut();
	
	public Integer addOutput(List<OutputConfig> outputConfigs);
	
}
