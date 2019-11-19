package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.mediaserver.dao.SubtitleDao;
import com.tech.mediaserver.entity.SubtitlePids;
import com.tech.mediaserver.service.SubtitleService;

@Service
@Transactional
public class SubtitleServiceImpl implements SubtitleService{

	@Autowired
	private SubtitleDao subtitleDao;

	@Override
	public List<SubtitlePids> getSubtitleByProId(Integer pro_id, Integer board_id) {
		return subtitleDao.getSubtitleByProId(pro_id, board_id);
	}

	@Override
	public Integer deletesubtitleByProIdList(List<Integer> pro_list) {
		return subtitleDao.deletesubtitleByProIdList(pro_list);
	}

	@Override
	public List<SubtitlePids> getAllSubtitle() {
		return subtitleDao.getAllSubtitle();
	}

	@Override
	public Integer deleteSubtitle() {
		return subtitleDao.deleteSubtitle();
	}

	@Override
	public Integer addSubtitle(List<SubtitlePids> subtitlePids) {
		return subtitleDao.addSubtitle(subtitlePids);
	}
	
}
