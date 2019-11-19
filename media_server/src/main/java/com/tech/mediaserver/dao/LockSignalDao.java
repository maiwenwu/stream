package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.LockSignal;

@Mapper
public interface LockSignalDao {

	public List<LockSignal> getAllLockSignal();
	
	public Integer deleteLockSignal();
	
	public Integer addLockSignal(@Param("lockSignals")List<LockSignal> lockSignals);
	
	public LockSignal getLockSignalByBoardIdAndModuleId(@Param("board_id")Integer board_id, @Param("module_id")Integer module_id);
	
	public Integer updateLockSignal(LockSignal lockSignal);
	
	public List<LockSignal> getLockSignalByTpList(@Param("tp_list")List<Integer> tp_list);
	
	public Integer updateLockSignalByTpList(@Param("tp_list")List<Integer> tp_list);
	
	public List<LockSignal> getLockSignalBySatList(@Param("sate_list")List<Integer> sate_list);
	
	public Integer updateLockSignalBySateList(@Param("sate_list")List<Integer> sate_list);
	
}
