package com.tech.mediaserver.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author chudichen
 * @date 2018/10/16
 */
//@Data
public class ChannelRecordDto extends ChannelRecordRegisterDto {
    @JsonProperty(value = "id")
    private Integer id;
    @JsonProperty(value = "select_app")
    private String selectApp;
    @JsonProperty(value = "state")
    private Integer state;
    @JsonProperty(value = "issue_state")
    private Integer issueState;
    @JsonProperty(value = "issue_time")
    private Date issueTime;
    @JsonProperty(value = "bitrate")
    private Integer bitrate;
    @JsonProperty(value = "speed")
    private Integer speed;
    @JsonProperty(value = "stored_time")
    private Integer storedTime;
    @JsonProperty(value = "origin")
    private String origin;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSelectApp() {
		return selectApp;
	}
	public void setSelectApp(String selectApp) {
		this.selectApp = selectApp;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getIssueState() {
		return issueState;
	}
	public void setIssueState(Integer issueState) {
		this.issueState = issueState;
	}
	public Date getIssueTime() {
		return issueTime;
	}
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}
	public Integer getBitrate() {
		return bitrate;
	}
	public void setBitrate(Integer bitrate) {
		this.bitrate = bitrate;
	}
	public Integer getSpeed() {
		return speed;
	}
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
	public Integer getStoredTime() {
		return storedTime;
	}
	public void setStoredTime(Integer storedTime) {
		this.storedTime = storedTime;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	@Override
	public String toString() {
		return "ChannelRecordDto [id=" + id + ", selectApp=" + selectApp + ", state=" + state + ", issueState="
				+ issueState + ", issueTime=" + issueTime + ", bitrate=" + bitrate + ", speed=" + speed
				+ ", storedTime=" + storedTime + ", origin=" + origin + "]";
	}

}
