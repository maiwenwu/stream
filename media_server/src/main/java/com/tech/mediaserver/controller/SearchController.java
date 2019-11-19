package com.tech.mediaserver.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.entity.LockSignal;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.LockSignalService;
import com.tech.mediaserver.service.RfService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.TpService;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;
import com.tech.mediaserver.utils.StreamSocketApi;

@RestController
@RequestMapping("/search")
public class SearchController {
	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();
	
	@Autowired
	private SatlliteService satlliteService;

	@Autowired
	private TpService tpService;

	@Autowired
	private RfService rfService;
	
	@Autowired
	private LockSignalService lockSignalService;
	
	@RequestMapping("/tpSearch")
	public MessageBean tpSearch(Integer board_id, Integer sat_id, Integer tp_id, String lnb_type) {

		Satllite satllite = satlliteService.getSatlliteBySatId(sat_id);
		Rf rf = rfService.getRfByBoardId(board_id);
		Transponder transponder = tpService.getTpByTpId(tp_id);
		
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("sat_id", sat_id);
		root.put("module_id", 0);
		root.put("sat_name", satllite.getName());
		root.put("sat_angle", satllite.getAngle());
		root.put("sat_dir", satllite.getDir());
		root.put("lnb_type",lnb_type);
		root.put("lnb_power", WebConstant.LNB_POWER[rf.getLnbPower()]);
		root.put("22k", WebConstant.TONE[rf.getTone()]);
		root.put("diseqc", rf.getDiseqc1_0());
		root.put("diseqc1.1", rf.getDiseqc1_1());

		JSONArray jsonArray = new JSONArray();

		JSONObject tpInfo = new JSONObject();
		tpInfo.put("tp_id", tp_id);
		tpInfo.put("fre", transponder.getFreq());
		tpInfo.put("pol", (transponder.getPolarization()==1)?"V":"H");
		tpInfo.put("sym", transponder.getSymbolRate());
		tpInfo.put("fec", transponder.getFec());
		jsonArray.add(tpInfo);

		root.put("tp_info", jsonArray);

		MessageBean msgBean = mStreamSocketApi.tpSearch(root.toString());
		return msgBean;
	}

	@RequestMapping("/satSearch")
	public MessageBean satSearch(Integer board_id, Integer sat_id, String lnb_type) {
		
		Satllite satllite = satlliteService.getSatlliteBySatId(sat_id);
		Rf rf = rfService.getRfByBoardId(board_id);
		
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("sat_id", sat_id);
		root.put("module_id", 0);
		root.put("sat_name", satllite.getName());
		root.put("sat_angle", satllite.getAngle());
		root.put("sat_dir", satllite.getDir());
		root.put("lnb_type", lnb_type);
		root.put("lnb_power", WebConstant.LNB_POWER[rf.getLnbPower()]);
		root.put("22k", WebConstant.TONE[rf.getTone()]);
		root.put("diseqc", rf.getDiseqc1_0());
		root.put("diseqc1.1", rf.getDiseqc1_1());
		
		List<Transponder> transponders = tpService.getTpBySatId(sat_id,0);

		JSONArray jsonArray = new JSONArray();
		for (int i = 0 ; i < transponders.size(); i ++) {
			JSONObject tpInfo = new JSONObject();
			tpInfo.put("tp_id", transponders.get(i).getId());
			tpInfo.put("fre", transponders.get(i).getFreq());
			tpInfo.put("fec", transponders.get(i).getFec());
			tpInfo.put("pol", (transponders.get(i).getPolarization()==1)?"V":"H");
			tpInfo.put("sym", transponders.get(i).getSymbolRate());
			jsonArray.add(tpInfo);
		}
		
		root.put("tp_info", jsonArray);

		MessageBean msgBean = mStreamSocketApi.satSearch(root.toString());
		return msgBean;
	}

	@RequestMapping("/blindSearch")
	public MessageBean blindSearch(Integer board_id, Integer sat_id, String lnb_type) {

		Rf rf = rfService.getRfByBoardId(board_id);
		
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("module_id", 0);
		root.put("sat_id", sat_id);
		root.put("lnb_type", lnb_type);
		root.put("lnb_power", WebConstant.LNB_POWER[rf.getLnbPower()]);
		root.put("22k", WebConstant.TONE[rf.getTone()]);
		root.put("diseqc", rf.getDiseqc1_0());
		root.put("diseqc1.1", rf.getDiseqc1_1());

		MessageBean msgBean = mStreamSocketApi.blindSearch(root.toString());
		return msgBean;
	}

	@RequestMapping("/lockSignal")
	public MessageBean lockSignal(Integer board_id,Integer module_id, Integer sat_id, Integer tp_id, String lnb_type) {
		return lockSignalService.lockSignal(board_id, module_id, sat_id, tp_id, lnb_type);
	}

	@RequestMapping("/getHeartInfo")
	public MessageBean getHeartInfo(Integer board_id, Integer module_id, Integer type) {
		JSONObject root = new JSONObject();
		
		root.put("board_id", board_id);
		root.put("module_id", module_id);
		root.put("type", type);

		MessageBean msgBean = mStreamSocketApi.getHeartInfo(root.toString());
		return msgBean;
	}
	
	@RequestMapping("/exitSearch")
	public MessageBean exitSearch(Integer board_id) {
		
		JSONObject root = new JSONObject();
		
		root.put("board_id", board_id);
		root.put("module_id", 0);

		MessageBean msgBean = mStreamSocketApi.exitSearch(root.toString());
		return msgBean;
	}

	@CrossOrigin
	@RequestMapping("/getAllSatllite")
	public List<Satllite> getAllSatllite() {
		return satlliteService.getAllSatllite();
	}

	@CrossOrigin
	@RequestMapping("/getSatInfoById")
	public JSONObject getSatInfoById(Integer id, Integer max_tp_id) {

		Satllite satllite = satlliteService.getSatlliteBySatId(id);
		List<Transponder> transponders = tpService.getTpBySatId(satllite.getId(),max_tp_id);

		JSONObject sat_tp_info = new JSONObject();
		sat_tp_info.put("tp_info", transponders);
		sat_tp_info.put("lnb_type", satllite.getLnbType());
		sat_tp_info.put("sat_name", satllite.getName());

		return sat_tp_info;
	}
	
	@RequestMapping("/getSearchStatus")
	public MessageBean getSearchStatus(Integer board_id, Integer module_id) {
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("module_id", module_id);
		MessageBean msgBean = mStreamSocketApi.getSearchStatus(root.toString());
		return msgBean;
		
	}
	
	@RequestMapping("/getOnlineInfo")
	public MessageBean getOnlineInfo() {
		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean messageBean = mStreamSocketApi.getOnlineInfo(root.toString());
		return messageBean;
	}
	
	@RequestMapping("/updateLockSignalByTpList")
	public RespBean updateLockSignalByTpList(@RequestBody Integer[] ids) {
		List<Integer> tp_list = Arrays.asList(ids);
		List<LockSignal> lockSignals = lockSignalService.getLockSignalByTpList(tp_list);
		
		for (LockSignal lockSignal : lockSignals) {
			JSONObject root = new JSONObject();
			root.put("board_id", lockSignal.getBoardId());
			root.put("module_id", lockSignal.getModuleId());
			root.put("signal_type",1);
			mStreamSocketApi.lockSignal(root.toString());
		}
		
		return RespBean.ok("", lockSignalService.updateLockSignalByTpList(tp_list));
	}
	
	@RequestMapping("/updateLockSignalBySateList")
	public RespBean updateLockSignalBySateList(@RequestBody Integer[] ids) {
		
		List<Integer> sate_list = Arrays.asList(ids);
		List<LockSignal> lockSignals = lockSignalService.getLockSignalBySateList(sate_list);
		
		for (LockSignal lockSignal : lockSignals) {
			JSONObject root = new JSONObject();
			root.put("board_id", lockSignal.getBoardId());
			root.put("module_id", lockSignal.getModuleId());
			root.put("signal_type",1);
			mStreamSocketApi.lockSignal(root.toString());
		}
		
		return RespBean.ok("", lockSignalService.updateLockSignalBySateList(sate_list));
	}
	
}
