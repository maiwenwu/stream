package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.mediaserver.dao.RfDao;
import com.tech.mediaserver.entity.Rf;
import com.tech.mediaserver.service.RfService;

@Service
@Transactional
public class RfServiceImpl implements RfService{

	@Autowired
	private RfDao rfDao;
	
	@Override
	public List<Rf> getAllRf() {
		return rfDao.getAllRf();
	}

	@Override
	public Rf getRfByBoardId(Integer board_id) {
		return rfDao.getRfByBoardId(board_id);
	}

	@Override
	public Integer saveRfInfo(Rf rf) {
		return rfDao.saveRfInfo(rf);
	}

	@Override
	public Integer deleteRf() {
		return rfDao.deleteRf();
	}

	@Override
	public Integer addRf(List<Rf> rfs) {
		return rfDao.addRf(rfs);
	}

}
