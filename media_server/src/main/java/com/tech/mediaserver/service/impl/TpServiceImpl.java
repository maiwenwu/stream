package com.tech.mediaserver.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.dao.TpDao;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.AudioPidsService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.SubtitleService;
import com.tech.mediaserver.service.TpService;

@Service
@Transactional
public class TpServiceImpl implements TpService{

	@Autowired
	private TpDao tpDao;
	
	@Autowired
	private TpService tpService;
	
	@Autowired
	private ProgramsService programsService;
	
	@Autowired
	private AudioPidsService audioService;
	
	@Autowired
	private SubtitleService subtitleService;
	
	@Autowired
	private OutputConfigService outputService;
	
	@Override
	public List<Transponder> getTpBySatId(Integer sat_id, Integer max_tp_id) {
		return tpDao.getTpBySatId(sat_id,max_tp_id);
	}

	@Override
	public Transponder getTpByTpId(Integer tp_id) {
		return tpDao.getTpByTpId(tp_id);
	}

	@Override
	public Integer getMaxTpId(Integer sat_id) {
		Integer maxTpId = tpDao.getMaxTpId(sat_id);
		if (maxTpId == null) {
			maxTpId = 0;
		}
		return tpDao.getMaxTpId(sat_id);
	}

	@Override
	public List<Integer> getSatIdByTpList(List<Integer> tpList) {
		return tpDao.getSatIdByTpList(tpList);
	}

	@Override
	public List<Transponder> getTpListWithPro(Integer sat_id, Integer board_id) {
		return tpDao.getTpListWithPro(sat_id,board_id);
	}

	@Override
	public PageInfo<Transponder> selectAll(Integer page, Integer size, String keyWord, Integer sat_id) {
		
		PageHelper.startPage(page, size);
		List<Transponder> transponders = tpDao.selectAllTp(sat_id, keyWord);
		
		PageInfo<Transponder> tPageInfo = new PageInfo<>(transponders);
		return tPageInfo;
	}

	@Override
	public List<Transponder> getTpListBySatList(List<Integer> sat_list) {
		return tpDao.getTpListBySatList(sat_list);
	}

	@Override
	public Integer deleteTpByIdList(List<Integer> id_list) {
		return tpDao.deleteTpByIdList(id_list);
	}

	@Override
	public Integer addTransponder(Transponder transponder) {
		return tpDao.addTransponder(transponder);
	}

	@Override
	public Integer updateTransponder(Transponder transponder) {
		return tpDao.updateTransponder(transponder);
	}

	@Override
	public List<Transponder> getSearchTp() {
		return tpDao.getSearchTp();
	}

	@Override
	public Integer deleteSearchTp() {
		return tpDao.deleteSearchTp();
	}

	@Override
	public List<Transponder> getAllTp() {
		return tpDao.getAllTp();
	}

	@Override
	public Integer deleteTp() {
		return tpDao.deleteTp();
	}

	@Override
	public Integer addTp(List<Transponder> transponders) {
		return tpDao.addTp(transponders);
	}

	@Override
	public Integer deleteTransponder(Integer[] ids) {
		List<Integer> tp_list = Arrays.asList(ids);
		Integer result = tpService.deleteTpByIdList(tp_list);
		
		//delete pro
		List<Programs> programs = programsService.getProByTpList(tp_list, -1);
		programsService.deleteProByTpListAndBoardId(tp_list, -1);
		
		List<Integer> proList = new ArrayList<>();
		for (int i = 0; i < programs.size(); i++) {
			proList.add(programs.get(i).getId());
		}
		
		//delete audio and subtitle
		if (proList.size() != 0) {
			audioService.deleteAudioByProIdList(proList);
			subtitleService.deletesubtitleByProIdList(proList);
		}
		
		return result;
	}

	@Override
	public Integer checkTpToDelete(Integer[] ids) {
		int result = 0;
		
		List<Integer> tp_id_list = Arrays.asList(ids);
		
		//get pro
		List<Programs> programs = programsService.getProByTpList(tp_id_list, -1);
		
		List<Integer> proList = new ArrayList<>();
		for (int i = 0; i < programs.size(); i++) {
			proList.add(programs.get(i).getId());
		}
		
		if (proList.size() != 0) {
			List<OutputConfig> outputConfigsPro = outputService.getOutputByProIdList(proList);
			result = 1;
			if (outputConfigsPro.size() != 0) {
				result = 2;
			}
		} 
		
		return result;
	}

	@Override
	public List<Transponder> getTpByTpInfo(Transponder transponder) {
		
		int freq_offset = 2;
		int symbol_offset = 4;
		
		if (transponder.getFreq() > 10000) {
			freq_offset = 4;
		}
		
		List<Transponder> transponders = tpDao.getTpByTpInfo(transponder.getSatId(), transponder.getFreq() - freq_offset, transponder.getFreq() + freq_offset, 
				transponder.getSymbolRate() - symbol_offset, transponder.getSymbolRate() + symbol_offset, transponder.getPolarization());
		
		return transponders;
	}

}
