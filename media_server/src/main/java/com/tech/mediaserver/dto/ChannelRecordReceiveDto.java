package com.tech.mediaserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author chudichen
 * @date 2018/10/16
 */
@Data
public class ChannelRecordReceiveDto extends ChannelRecordRegisterDto {
    @JsonProperty(value = "state")
    private Integer state;
    @JsonProperty(value = "bitrate")
    private Integer bitrate;
    @JsonProperty(value = "speed")
    private Integer speed;
    @JsonProperty(value = "stored_time")
    private Integer storedTime;
}
