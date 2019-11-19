package com.tech.mediaserver.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tech.mediaserver.dto.HeartBeatDto;
import com.tech.mediaserver.utils.Pagination;
import com.tech.mediaserver.utils.RedisUtil;
import com.tech.mediaserver.vo.AppListVo;
import com.tech.mediaserver.vo.PageVo;


/**
 * @author chudichen
 * @date 2018/9/19
 */
@Service
public class HeartbeatService {

    @Autowired
    private RedisUtil redisUtil;
    
    private static final String KEY = "app:id:";

    public void saveHeartbeat(HeartBeatDto heartBeatDto) {
        redisUtil.setOneMinute(KEY + heartBeatDto.getModuleId(), heartBeatDto);
    }

    public PageVo<HeartBeatDto> getHeartbeatPage(Pagination pagination) {
        Set<String> keys =  redisUtil.keys(KEY);
        PageVo<HeartBeatDto> page = new PageVo<>();
        Object[] keyArray = keys.toArray();
        int startIndex = (pagination.getCurrent() - 1) * pagination.getPageSize();
        int endIndex = startIndex + pagination.getPageSize() < keyArray.length
                ? pagination.getCurrent() + pagination.getPageSize()
                : keyArray.length;
        Set<String> pageKeys = new HashSet<>();
        while (startIndex < endIndex) {
            pageKeys.add(keyArray[startIndex++].toString());
        }
        pagination.setTotal(keyArray.length);
        page.setPagination(pagination);
        page.setList(getHeartBeatDtoList(pageKeys));
        return page;
    }

    public AppListVo getApplist() {
        Set<String> keys =  redisUtil.keys(KEY);
        return new AppListVo(getHeartBeatDtoList(keys));
    }

    HeartBeatDto getHeartBeat(String id) {
        return redisUtil.get(KEY + id, HeartBeatDto.class);
    }

    private List<HeartBeatDto> getHeartBeatDtoList(Set<String> keys) {
        List<HeartBeatDto> heartBeatDtoList = new ArrayList<>();
        keys.forEach(key -> heartBeatDtoList.add(redisUtil.get(key, HeartBeatDto.class)));
        return heartBeatDtoList;
    }

    public PageVo<HeartBeatDto> getApplistByModuleType(String moduleType) {
        AppListVo appList = getApplist();
        List<HeartBeatDto> heartBeatDtoList = new ArrayList<>();
        appList.getAppList().forEach(heartBeatDto -> {
            if (StringUtils.equals(heartBeatDto.getModuleType(), moduleType)) {
                heartBeatDtoList.add(heartBeatDto);
            }
        });
        PageVo<HeartBeatDto> page = new PageVo<>();
        page.setType(moduleType);
        page.setList(heartBeatDtoList);
        return page;
    }

}
