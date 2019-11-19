package com.tech.mediaserver.entity;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Data;

/**
 * @author chudichen
 * @date 2018/10/16
 */
@Data
@ExcelTarget("channelRecord")
public class ChannelRecord implements IExcelModel {
	@Excel(name = "Id", orderNum="0")
    private Integer id;
    
    @Excel(name = "Channel Id", orderNum="1")
    @Size(min=1,max=128,message="节目标识的长度在128以内")
    private String channelId;
    
    @Excel(name = "Channel Name", orderNum="2",  width=30)
    @Size(min=1,max=512,message="节目名称的长度在512以内")
    private String channelName;
    
    @Excel(name = "Source Url", orderNum="3", width=30)
    private String sourceUrl;
    
    @Excel(name = "On Demand", orderNum="4",  width=10)
    @NotNull(message="是否按需拉流不能为空")
    private Integer onDemand;
    
    @Excel(name = "Bitrate", orderNum="5", isImportField="false")
    private Integer bitrate;
    
    @Excel(name = "Speed", orderNum="6", isImportField="false")
    private Integer speed;
    
    @Excel(name = "Origin", orderNum="7")
    @Size(max=512,message="来源的长度在512以内")
    private String origin;
    
    @Excel(name = "Select App", orderNum="8")
    @NotNull(message="应用ID不能为空")
    private String selectApp;
    
    @Excel(name = "Store Time", orderNum="9")
    @Min(value=0,message="录制时长不能小于0")
    @Max(value=168,message="录制时不能大于168")
    private Integer storeTime;
    
    @Excel(name = "Stored Time", orderNum="10", isImportField="false",  width=10)
    private Integer storedTime;
    
    private Integer state;
    
    @Excel(name = "Board Id", orderNum="11", isImportField="false")
    private Integer boardId;
    
    @Excel(name = "Module Id", orderNum="12", isImportField="false")
    private Integer moduleId;
    
    @Excel(name = "Hls Out Mode", orderNum="13", isImportField="false",  width=15)
	private Integer hlsOutMode;
    
    @Excel(name = "Hls Out Ip", orderNum="14", isImportField="false",  width=20)
	private String hlsOutIp;
    
    @Excel(name = "Hls Out Port", orderNum="15", isImportField="false")
	private Integer hlsOutPort;
    
    private Integer type;
    
    private Integer issueState;
    private Date createTime;
    private Date updateTime;
    
    private Date issueTime;
    
//    @Excel(name="错误提示",orderNum="10",width=50, isImportField="false")
	private String errorMsg;
    
}
