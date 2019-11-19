package com.tech.mediaserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.mediaserver.dao.AudioPidsDao;
import com.tech.mediaserver.entity.AudioPids;
import com.tech.mediaserver.service.AudioPidsService;

@Service
@Transactional
public class AudioPidsServiceImpl implements AudioPidsService{

	@Autowired
	private AudioPidsDao audioPidsDao;

	@Override
	public List<AudioPids> getAudioByProId(Integer pro_id, Integer board_id) {
		return audioPidsDao.getAudioByProId(pro_id,board_id);
	}

	@Override
	public Integer deleteAudioByProIdList(List<Integer> pro_list) {
		return audioPidsDao.deleteAudioByProIdList(pro_list);
	}

	@Override
	public List<AudioPids> getAudioByProList(List<Integer> pro_list, Integer board_id) {
		return audioPidsDao.getAudioByProList(pro_list, board_id);
	}

	@Override
	public List<AudioPids> getAllAudioPids() {
		return audioPidsDao.getAllAudioPids();
	}

	@Override
	public Integer deleteAudioPids() {
		return audioPidsDao.deleteAudioPids();
	}

	@Override
	public Integer addAudioPids(List<AudioPids> audioPids) {
		return audioPidsDao.addAudioPids(audioPids);
	}
	
}
