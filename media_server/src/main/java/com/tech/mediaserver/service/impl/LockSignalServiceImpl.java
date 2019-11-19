package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.dao.LockSignalDao;
import com.tech.mediaserver.entity.LockSignal;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.entity.Satllite;
import com.tech.mediaserver.entity.Transponder;
import com.tech.mediaserver.service.LockSignalService;
import com.tech.mediaserver.service.RfService;
import com.tech.mediaserver.service.SatlliteService;
import com.tech.mediaserver.service.TpService;
import com.tech.mediaserver.utils.StreamSocketApi;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;

@Service
@Transactional
public class LockSignalServiceImpl implements LockSignalService{

	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();
	
	@Autowired
	private LockSignalDao lockSignalDao;
	
	@Autowired
	private SatlliteService satlliteService;

	@Autowired
	private TpService tpService;

	@Autowired
	private RfService rfService;
	
	@Override
	public Integer updateLockSignal(LockSignal lockSignal) {
		return lockSignalDao.updateLockSignal(lockSignal);
	}

	@Override
	public LockSignal getLockSignalByBoardIdAndModuleId(Integer board_id, Integer module_id) {
		return lockSignalDao.getLockSignalByBoardIdAndModuleId(board_id, module_id);
	}

	@Override
	public List<LockSignal> getAllLockSignal() {
		return lockSignalDao.getAllLockSignal();
	}

	@Override
	public Integer deleteLockSignal() {
		return lockSignalDao.deleteLockSignal();
	}

	@Override
	public Integer addLockSignal(List<LockSignal> lockSignals) {
		return lockSignalDao.addLockSignal(lockSignals);
	}

	@Override
	public MessageBean lockSignal(Integer board_id,Integer module_id, Integer sat_id, Integer tp_id, String lnb_type) {
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("module_id", module_id);
		
		if (sat_id == null && tp_id == null && lnb_type == "") {
			root.put("signal_type",1);
		} else {
			root.put("signal_type",0);
			Satllite satllite = satlliteService.getSatlliteBySatId(sat_id);
			Rf rf = rfService.getRfByBoardId(board_id);
			Transponder transponder = tpService.getTpByTpId(tp_id);
			
			root.put("sat_id",satllite.getId());
			root.put("sat_name", satllite.getName());
			root.put("sat_angle", satllite.getAngle());
			root.put("sat_dir", satllite.getDir());
			root.put("lnb_type", lnb_type);
			root.put("lnb_power", WebConstant.LNB_POWER[rf.getLnbPower()]);
			root.put("22k", WebConstant.TONE[rf.getTone()]);;
			root.put("diseqc", rf.getDiseqc1_0());
			root.put("diseqc1.1", rf.getDiseqc1_1());

			JSONArray jsonArray = new JSONArray();

			JSONObject tpInfo = new JSONObject();
			tpInfo.put("fre", transponder.getFreq());
			tpInfo.put("pol", (transponder.getPolarization()==1)?"V":"H");
			tpInfo.put("sym", transponder.getSymbolRate());
			tpInfo.put("fec", transponder.getFec());
			tpInfo.put("tp_id", transponder.getId());
			jsonArray.add(tpInfo);

			root.put("tp_info", jsonArray);
			
		}

		MessageBean msgBean = mStreamSocketApi.lockSignal(root.toString());
		return msgBean;
	}


	@Override
	public List<LockSignal> getLockSignalByTpList(List<Integer> tp_list) {
		return lockSignalDao.getLockSignalByTpList(tp_list);
	}

	@Override
	public Integer updateLockSignalByTpList(List<Integer> tp_list) {
		return lockSignalDao.updateLockSignalByTpList(tp_list);
	}

	@Override
	public List<LockSignal> getLockSignalBySateList(List<Integer> sate_list) {
		return lockSignalDao.getLockSignalBySatList(sate_list);
	}

	@Override
	public Integer updateLockSignalBySateList(List<Integer> sate_list) {
		return lockSignalDao.updateLockSignalBySateList(sate_list);
	}

}
