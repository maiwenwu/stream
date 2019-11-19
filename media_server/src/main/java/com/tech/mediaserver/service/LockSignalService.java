package com.tech.mediaserver.service;

import java.util.List;
import com.tech.mediaserver.entity.LockSignal;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;

public interface LockSignalService {

	public List<LockSignal> getAllLockSignal();
	
	public Integer deleteLockSignal();
	
	public Integer addLockSignal(List<LockSignal> lockSignals);
	
	public LockSignal getLockSignalByBoardIdAndModuleId(Integer board_id, Integer module_id);
	
	public Integer updateLockSignal(LockSignal lockSignal);
	
	public MessageBean lockSignal(Integer board_id,Integer module_id, Integer sat_id, Integer tp_id, String lnb_type);
	
	public List<LockSignal> getLockSignalByTpList(List<Integer> tp_list);
	
	public Integer updateLockSignalByTpList(List<Integer> tp_list);
	
	public List<LockSignal> getLockSignalBySateList(List<Integer> sate_list);
	
	public Integer updateLockSignalBySateList(List<Integer> sate_list);
	
}
