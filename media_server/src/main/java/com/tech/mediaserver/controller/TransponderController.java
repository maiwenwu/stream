package com.tech.mediaserver.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.ProgramsService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.TpService;

@RestController
@RequestMapping("/transponder")
public class TransponderController {

	public final static Logger logger =  LoggerFactory.getLogger(TransponderController.class);
	
	@Autowired
	private SatlliteService satlliteService;
	
	@Autowired
	private TpService tpService;
	
	@Autowired
	private ProgramsService programsService;
	
	@RequestMapping("/selectAllTp")
	public PageInfo<Transponder> selectAllTp(Integer page,Integer size,String keyWord,Integer sat_id) {
		return tpService.selectAll(page, size, keyWord, sat_id);
	}
	
	@RequestMapping("/getTpInfoById")
	public Transponder getTpInfoById(Integer id) {
		Transponder transponder = tpService.getTpByTpId(id);
		return transponder;
	}
	
	@RequestMapping("/addTransponder")
	public RespBean addTransponder(@RequestBody Transponder transponder) {
		int result = 1;
		
		if (transponder.getTsId() == null) {
			transponder.setTsId(0);
		}
		if (transponder.getOnId() == null) {
			transponder.setOnId(0);
		}
		if (transponder.getEitId() == null) {
			transponder.setEitId(0);
		}
		
		List<Transponder> transponders = tpService.getTpByTpInfo(transponder);
		if (transponders.size() != 0) {
			result = 0;
		}
		
		if (result == 1) {
			tpService.addTransponder(transponder);
		} 
		
		return RespBean.ok("", result);
	}
	
	@RequestMapping("/updateTransponder")
	public RespBean updateTransponder(@RequestBody Transponder transponder) {
		if (transponder.getTsId() == null) {
			transponder.setTsId(0);
		}
		if (transponder.getOnId() == null) {
			transponder.setOnId(0);
		}
		if (transponder.getEitId() == null) {
			transponder.setEitId(0);
		}
		Integer result = tpService.updateTransponder(transponder);
		
		return RespBean.ok("", result);
	}
	
	@RequestMapping("/deleteProgram")
	public RespBean deleteProgram(@RequestBody Integer[] ids) {
		List<Integer> tp_list = Arrays.asList(ids);
		
		//delete pro
		Integer result = programsService.deleteProByTpListAndBoardId(tp_list, -1);
		return RespBean.ok("", result);
	}
	
	@RequestMapping("/deleteTransponder")
	public RespBean deleteTransponder(@RequestBody Integer[] ids) {
		return RespBean.ok("", tpService.deleteTransponder(ids));
	}
	
	@RequestMapping("/checkTpToDelete")
	public RespBean checkTpToDelete(@RequestBody Integer[] ids) {
		return RespBean.ok("", tpService.checkTpToDelete(ids));
	}
	
	@RequestMapping("/deleteSearchTp")
	public RespBean deleteSearchTp() {
		Integer result = tpService.deleteSearchTp();
		return RespBean.ok("", result);
	}
	
	@RequestMapping("/getSearchTp")
	public JSONObject getSearchTp(Integer sat_id) {
		Satllite satllite = satlliteService.getSatlliteBySatId(sat_id);

		List<Transponder> transponders = tpService.getSearchTp();

		JSONObject sat_tp_info = new JSONObject();
		sat_tp_info.put("tp_info", transponders);
		sat_tp_info.put("lnb_type", satllite.getLnbType());
		sat_tp_info.put("sat_name", satllite.getName());
		return sat_tp_info;
	}
	
}
