package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.AudioPids;

@Mapper
public interface AudioPidsDao {
	
	public List<AudioPids> getAllAudioPids();
	
	public Integer deleteAudioPids();
	
	public Integer addAudioPids(@Param("audioPids")List<AudioPids> audioPids);

	public List<AudioPids> getAudioByProId(@Param("program_id")Integer pro_id,@Param("board_id") Integer board_id);
	
	public Integer deleteAudioByProIdList(@Param("pro_list")List<Integer> pro_list);
	
	public List<AudioPids> getAudioByProList(@Param("proIdList") List<Integer> pro_list, @Param("board_id") Integer board_id);
}
