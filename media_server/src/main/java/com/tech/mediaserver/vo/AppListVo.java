package com.tech.mediaserver.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech.mediaserver.dto.HeartBeatDto;

import lombok.Data;

import java.util.List;

/**
 * @author chudichen
 * @date 2018/10/9
 */
@Data
public class AppListVo {
    @JsonProperty(value = "app_list")
    private List<HeartBeatDto> appList;

    public AppListVo(List<HeartBeatDto> appList) {
        this.appList = appList;
    }

    public AppListVo() {
    }
}
