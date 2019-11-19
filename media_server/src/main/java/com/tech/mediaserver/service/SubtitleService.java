package com.tech.mediaserver.service;

import java.util.List;


import com.tech.mediaserver.entity.SubtitlePids;

public interface SubtitleService {

	public List<SubtitlePids> getSubtitleByProId(Integer pro_id,Integer board_id);

	public Integer deletesubtitleByProIdList(List<Integer> pro_list);
	
	public List<SubtitlePids> getAllSubtitle();
	
	public Integer deleteSubtitle();
	
	public Integer addSubtitle(List<SubtitlePids> subtitlePids);
	
}
