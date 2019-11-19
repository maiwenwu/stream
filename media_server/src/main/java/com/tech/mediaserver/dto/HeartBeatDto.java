package com.tech.mediaserver.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/9/14
 */
@Data
public class HeartBeatDto {
	
    @JsonProperty(value = "module_id")
    private Integer moduleId;
    
    @JsonProperty(value = "module_type")
    private String moduleType;
    
    @JsonProperty(value = "module_port")
    private Integer modulePort;
    
    @JsonProperty(value = "server_ip")
    private List<String> serverIp;
    
    @JsonProperty(value = "server_time")
    private String serverTime;
    
    @JsonProperty(value = "boot_time")
    private String bootTime;
    
    @JsonProperty(value = "software_version")
    private String softwareVersion;
    
    @JsonProperty(value = "build_time")
    private String buildTime;
    
    @JsonProperty(value = "status")
    private Integer status = 1;
    
    @JsonProperty(value = "auth")
    private Integer auth;
}
