package com.tech.mediaserver.utils;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author chudichen
 * @date 2018/9/19
 */

public class Pagination {
   
	private Integer total;
    private Integer pageSize;
    private Integer current;
    @JsonProperty(value = "channel_name")
    private String channelName;
    private String channelId;
    @JsonProperty(value = "issue_state")
    private Integer issueState;
    private Integer state;
    @JsonProperty(value = "app_id")
    private String appId;
    @JsonProperty(value = "on_demand")
    private Integer onDemand;
    @JsonProperty(value = "src")
    private String src;
    @JsonProperty(value = "device")
    private String device;
    @JsonProperty(value = "sorter")
    private String sorter;
    
    public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getCurrent() {
		return current;
	}
	public void setCurrent(Integer current) {
		this.current = current;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public Integer getIssueState() {
		return issueState;
	}
	public void setIssueState(Integer issueState) {
		this.issueState = issueState;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public Integer getOnDemand() {
		return onDemand;
	}
	public void setOnDemand(Integer onDemand) {
		this.onDemand = onDemand;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getSorter() {
		return sorter;
	}
	public void setSorter(String sorter) {
		this.sorter = sorter;
	}
	@Override
	public String toString() {
		return "Pagination [total=" + total + ", pageSize=" + pageSize + ", current=" + current + ", channelName="
				+ channelName + ", channelId=" + channelId + ", issueState=" + issueState + ", state=" + state
				+ ", appId=" + appId + ", onDemand=" + onDemand + ", src=" + src + ", device=" + device + ", sorter="
				+ sorter + "]";
	}
	
	
	
    
}
