package com.tech.mediaserver.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/10/16
 */
//@Data
public class ChannelRecordRegisterDto {
	@JsonProperty("channel_id")
	@JSONField(name="channel_id")
    private String channelId;
	
	@JsonProperty("channel_name")
	@JSONField(name="channel_name")
    private String channelName;
	
	@JsonProperty("origin")
	@JSONField(name="origin")
    private String origin;
	
	@JsonProperty("source_url")
	@JSONField(name="source_url")
    private String sourceUrl;
	
	@JsonProperty("on_demand")
	@JSONField(name="on_demand")
    private Integer onDemand;
	
	@JsonProperty("store_time")
	@JSONField(name="store_time")
    private Integer storeTime;
	
	@JsonProperty("board_id")
	@JSONField(name="board_id")
    private Integer boardId;
    
	@JsonProperty("module_id")
	@JSONField(name="module_id")
    private Integer moduleId;
	
	@JsonProperty("hls_out_mode")
	@JSONField(name="hls_out_mode")
	private Integer hlsOutMode;
	
	@JsonProperty("hls_out_ip")
	@JSONField(name="hls_out_ip")
	private String hlsOutIp;
	
	@JsonProperty("hls_out_port")
	@JSONField(name="hls_out_port")
	private Integer hlsOutPort;
	
	@JsonProperty("type")
	@JSONField(name="type")
	private Integer type;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public Integer getOnDemand() {
		return onDemand;
	}

	public void setOnDemand(Integer onDemand) {
		this.onDemand = onDemand;
	}

	public Integer getStoreTime() {
		return storeTime;
	}

	public void setStoreTime(Integer storeTime) {
		this.storeTime = storeTime;
	}

	public Integer getBoardId() {
		return boardId;
	}

	public void setBoardId(Integer boardId) {
		this.boardId = boardId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}
	
	public Integer getHlsOutMode() {
		return hlsOutMode;
	}

	public void setHlsOutMode(Integer hlsOutMode) {
		this.hlsOutMode = hlsOutMode;
	}

	public String getHlsOutIp() {
		return hlsOutIp;
	}

	public void setHlsOutIp(String hlsOutIp) {
		this.hlsOutIp = hlsOutIp;
	}

	public Integer getHlsOutPort() {
		return hlsOutPort;
	}

	public void setHlsOutPort(Integer hlsOutPort) {
		this.hlsOutPort = hlsOutPort;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ChannelRecordRegisterDto [channelId=" + channelId + ", channelName=" + channelName + ", origin="
				+ origin + ", sourceUrl=" + sourceUrl + ", onDemand=" + onDemand + ", storeTime=" + storeTime
				+ ", boardId=" + boardId + ", moduleId=" + moduleId + ", hlsOutMode=" + hlsOutMode + ", hlsOutIp="
				+ hlsOutIp + ", hlsOutPort=" + hlsOutPort + ", type=" + type + "]";
	}

}
