package com.tech.mediaserver.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.tech.mediaserver.entity.ChannelRecord;

/**
 * @author chudichen
 * @date 2018/10/16
 */
@Repository
@Mapper
public interface ChannelRecordDao extends BaseDao<ChannelRecord>{

	List<String> selectAllChannelIds();
}
