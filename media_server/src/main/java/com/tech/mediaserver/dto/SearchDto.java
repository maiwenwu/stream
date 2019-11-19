package com.tech.mediaserver.dto;

import java.util.List;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/10/31
 */
@Data
public class SearchDto {
    private String appId;
    private String channelId;
    private String channelName;
    private Integer issueState;
    private String src;
    private String device;
    private Integer state;
    private List<Integer> ids;
}
