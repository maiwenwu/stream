package com.tech.mediaserver.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.dao.SatlliteDao;
import com.tech.mediaserver.entity.OutputConfig;
import com.tech.mediaserver.entity.Programs;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.AudioPidsService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.SubtitleService;
import com.tech.mediaserver.service.TpService;
import com.tech.mediaserver.utils.ExcelUtil;

@Service
@Transactional
public class SatlliteServiceImpl implements SatlliteService{

	@Autowired
	SatlliteDao satlliteDao;
	
	@Autowired
	private AudioPidsService audioService;
	
	@Autowired
	private SubtitleService subtitleService;
	
	@Autowired
	private TpService tpService;
	
	@Autowired
	private ProgramsService programsService;
	
	@Autowired
	private OutputConfigService outputService;
	
	@Override
	public List<Satllite> getAllSatllite() {
		return satlliteDao.getAllSatllite();
	}

	@Override
	public Satllite getSatlliteBySatId(Integer id) {
		return satlliteDao.getSatlliteBySatId(id);
	}

	@Override
	public Integer addSatllite(Satllite satllite) {
		return satlliteDao.addSatllite(satllite);
	}

	@Override
	public List<Satllite> getSatBySatList(List<Integer> satList) {
		return satlliteDao.getSatBySatList(satList);
	}

	@Override
	public PageInfo<Satllite> selectAll(Integer page, Integer size, String keyWord) {
		PageHelper.startPage(page, size);
		List<Satllite> satllites = satlliteDao.selectAllSatllite(keyWord);
		
		PageInfo<Satllite> sPageInfo = new PageInfo<>(satllites);
		return sPageInfo;
	
	}

	@Override
	public List<Satllite> insertSatlliteList(MultipartFile file) {
		List<Satllite> satllite_list = null;
		try {
			satllite_list = ExcelUtil.importExcel(file, 1, Satllite.class);
			if(satllite_list == null) {
				return satllite_list;
			}
			
//			addNum = satlliteDao.insertSatlliteList(satllite_list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		
//		if(addNum > 0) {
//			return true;
//		}
//		else {
//			return false;
//		}
		
		return satllite_list;
		
	}

	@Override
	public Integer updateSatllite(Satllite satllite) {
		return satlliteDao.updateSatllite(satllite);
	}

	@Override
	public Integer deleteSatlliteByIds(Integer[] ids) {
		return satlliteDao.deleteSatlliteByIds(ids);
	}

	@Override
	public Integer deleteSatellite() {
		return satlliteDao.deleteSatellite();
	}

	@Override
	public Integer addSatelliteList(List<Satllite> satllites) {
		return satlliteDao.addSatelliteList(satllites);
	}

	@Override
	public Integer checkSatToDelete(Integer[] ids) {

		int result = 0;
		
		List<Integer> sat_id_list = Arrays.asList(ids);
		
		//get TpList
		List<Transponder> transponders = tpService.getTpListBySatList(sat_id_list);
		
		List<Integer> tp_id_list = new ArrayList<>();
		for(Transponder transponder : transponders) {
			tp_id_list.add(transponder.getId());
		}
		
		if (tp_id_list.size() != 0) {
			//get pro
			List<Programs> programs = programsService.getProByTpList(tp_id_list, -1);
			
			List<Integer> proList = new ArrayList<>();
			for (int i = 0; i < programs.size(); i++) {
				proList.add(programs.get(i).getId());
			}
			
			if (proList.size() == 0) {
			} else {
				List<OutputConfig> outputConfigsPro = outputService.getOutputByProIdList(proList);
				result = 1;
				if (outputConfigsPro.size() != 0) {
					result = 2;
				}
			}
		} 
		return result;
	}

	@Override
	public Integer deleteSatllite(Integer[] ids) {
		List<Integer> sat_list = Arrays.asList(ids);
		
		//get TpList
		List<Transponder> transponders = tpService.getTpListBySatList(sat_list);
		
		List<Integer> id_list = new ArrayList<>();
		for(Transponder transponder : transponders) {
			id_list.add(transponder.getId());
		}
		
		if (id_list.size() != 0) {
			//delete tp
			tpService.deleteTpByIdList(id_list);
			
			//delete pro
			List<Programs> programs = programsService.getProByTpList(id_list, -1);
			programsService.deleteProByTpListAndBoardId(id_list, -1);
			
			List<Integer> proList = new ArrayList<>();
			for (Programs program : programs) {
				proList.add(program.getId());
			}
			
			//delete audio and subtitle
			if (proList.size() != 0) {
				audioService.deleteAudioByProIdList(proList);
				subtitleService.deletesubtitleByProIdList(proList);
			}
		}
		return deleteSatlliteByIds(ids);
	}

}
