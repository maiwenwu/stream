package com.tech.mediaserver.dto;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author chudichen
 * @date 2018/9/27
 */
//@Data
public class ChannelIdListDto {

    @JsonProperty(value = "channel_id_list")
    private String[] idList;

    public ChannelIdListDto() {
    }

    public ChannelIdListDto(String[] idList) {
        this.idList = idList;
    }

	public String[] getIdList() {
		return idList;
	}

	public void setIdList(String[] idList) {
		this.idList = idList;
	}

	@Override
	public String toString() {
		return "ChannelIdListDto [idList=" + Arrays.toString(idList) + ", getIdList()=" + Arrays.toString(getIdList())
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
    
}
