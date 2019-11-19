package com.tech.mediaserver.utils;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/9/27
 */
@Data
public class PageBean {
    private int start;
    private int size;
    private String channelName;
    private Integer state;
    private String appId;
    private String src;
    private String device;
    private String channelId;
    private Integer issueState;
    private Integer onDemand;
    private String sorter;
}
