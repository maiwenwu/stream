package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.dao.OutputConfigDao;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.service.OutputConfigService;

@Service
@Transactional
public class OutputConfigServiceImpl implements OutputConfigService{

	@Autowired
	private OutputConfigDao outputDao;
	
	@Override
	public List<OutputConfig> getOutputById(Integer board_id, Integer module_id) {
		return outputDao.getOutputById(board_id, module_id);
	}

	@Override
	public Integer addOutputConfig(OutputConfig outputConfigs) {
		return outputDao.addOutputConfig(outputConfigs);
	}

	@Override
	public OutputConfig getOutput(Integer id) {
		return outputDao.getOutput(id);
	}

	@Override
	public Integer updateOutput(OutputConfig outputConfig) {
		return outputDao.updateOutput(outputConfig);
	}

	@Override
	public Integer deleteOutput(Integer id) {
		return outputDao.deleteOutput(id);
	}

	@Override
	public Integer deleteAllStreamingByBoardId(Integer board_id) {
		return outputDao.deleteAllStreamingByBoardId(board_id);
	}

	@Override
	public List<OutputConfig> getOutputByBoardId(Integer board_id) {
		return outputDao.getOutputBySatId(board_id);
	}

	@Override
	public List<OutputConfig> getAllOutPut() {
		return outputDao.getAllOutPut();
	}

	@Override
	public List<OutputConfig> getOutputBySatIdList(List<Integer> sat_id_list) {
		return outputDao.getOutputBySatIdList(sat_id_list);
	}

	@Override
	public List<OutputConfig> getOutputByTpIdList(List<Integer> tp_id_list) {
		return outputDao.getOutputByTpIdList(tp_id_list);
	}

	@Override
	public List<OutputConfig> getOutputByProIdList(List<Integer> pro_id_list) {
		return outputDao.getOutputByProIdList(pro_id_list);
	}

	@Override
	public List<OutputConfig> getOutputByBoardIdAndSatIdAndTpId(Integer board_id, Integer sat_id, Integer tp_id) {
		return outputDao.getOutputByBoardIdAndSatIdAndTpId(board_id, sat_id, tp_id);
	}

	@Override
	public Integer deleteOutputByIdList(List<Integer> id_list) {
		return outputDao.deleteOutputByIdList(id_list);
	}

	@Override
	public List<OutputConfig> getOutputByIdList(List<Integer> id_list) {
		return outputDao.getOutputByIdList(id_list);
	}

	@Override
	public PageInfo<OutputConfig> selectAllStreams(Integer board_id, Integer module_id, Integer page, Integer size) {
		
		PageHelper.startPage(page, size);
		
		List<OutputConfig> outputConfigs = outputDao.getOutputById(board_id,module_id);
		
		PageInfo<OutputConfig> tPageInfo = new PageInfo<>(outputConfigs);
		
		return tPageInfo;
	}

	@Override
	public Integer deleteAllOutPut() {
		return outputDao.deleteAllOutPut();
	}

	@Override
	public Integer addOutput(List<OutputConfig> outputConfigs) {
		return outputDao.addOutput(outputConfigs);
	}

}
