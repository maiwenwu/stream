package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tech.mediaserver.entity.SubtitlePids;

@Mapper
public interface SubtitleDao {

	public List<SubtitlePids> getSubtitleByProId(@Param("pro_id")Integer pro_id,@Param("board_id") Integer board_id);
	
	public Integer deletesubtitleByProIdList(@Param("pro_list")List<Integer> pro_list);
	
	public List<SubtitlePids> getAllSubtitle();
	
	public Integer deleteSubtitle();
	
	public Integer addSubtitle(@Param("subtitlePids")List<SubtitlePids> subtitlePids);
}
