package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.dao.ProgramsDao;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.service.ProgramsService;

@Service
@Transactional
public class ProgramsServiceImpl implements ProgramsService{

	@Autowired
	private ProgramsDao programsDao;

	@Override
	public List<Programs> getAllPrograms() {
		return programsDao.getAllPrograms();
	}

	@Override
	public Integer deleteProByTpListAndBoardId(List<Integer> tList, Integer board_id) {
		return programsDao.deleteProByTpListAndBoardId(tList,board_id);
	}

	@Override
	public Integer deleteProByTpId(Integer tp_id) {
		return programsDao.deleteProByTpId(tp_id);
	}

	@Override
	public List<Integer> getTpIdList(Integer board_id, Integer module_id) {
		return programsDao.getTpIdList(board_id, module_id);
	}

	@Override
	public List<Programs> getProgramsByTpId(Integer tp_id, Integer board_id) {
		return programsDao.getProgramsByTpId(tp_id, board_id);
	}

	@Override
	public Programs getProById(Integer pro_id, Integer board_id) {
		return programsDao.getProById(pro_id, board_id);
	}

	@Override
	public List<Programs> getProByTpList(List<Integer> tp_list, Integer board_id) {
		return programsDao.getProByTpList(tp_list, board_id);
	}

	@Override
	public List<Programs> getProByBoardId(Integer board_id) {
		return programsDao.getProByBoardId(board_id);
	}

	@Override
	public Integer deleteProByBoardId(Integer board_id) {
		return programsDao.deleteProByBoardId(board_id);
	}
	
	@Override
	public PageInfo<Programs> selectAllPro(Integer page, Integer size, String keyWord, List<Integer> tp_list,Integer board_id) {
		
		PageHelper.startPage(page, size);
		List<Programs> program_list = programsDao.selectAllPro(tp_list, keyWord,board_id);
		
		PageInfo<Programs> tPageInfo = new PageInfo<>(program_list);
		return tPageInfo;
	}

	@Override
	public Integer updateProgram(Programs programs) {
		return programsDao.updateProgram(programs);
	}

	@Override
	public Integer deleteProgramByIds(Integer[] ids) {
		return programsDao.deleteProgramByIds(ids);
	}

	@Override
	public List<Programs> getSearchProgramsByTpId(Integer tp_id, Integer board_id) {
		return programsDao.getSearchProgramsByTpId(tp_id, board_id);
	}

	@Override
	public Integer deleteSearchPrograms() {
		return programsDao.deleteSearchPrograms();
	}

	@Override
	public Integer deletePrograms() {
		return programsDao.deletePrograms();
	}

	@Override
	public Integer addPrograms(List<Programs> programs) {
		return programsDao.addPrograms(programs);
	}
	
}
