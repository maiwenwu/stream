package com.tech.mediaserver.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.AudioPids;

public interface AudioPidsService {
	
	public List<AudioPids> getAllAudioPids();
	
	public Integer deleteAudioPids();
	
	public Integer addAudioPids(List<AudioPids> audioPids);

	public List<AudioPids> getAudioByProId(Integer pro_id,Integer board_id);
	
	public Integer deleteAudioByProIdList(List<Integer> pro_list);
	
	public List<AudioPids> getAudioByProList(@Param("proIdList") List<Integer> pro_list, @Param("board_id") Integer board_id);
}
