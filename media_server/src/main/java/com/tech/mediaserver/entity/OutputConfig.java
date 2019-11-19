package com.tech.mediaserver.entity;


import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class OutputConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	@Excel(name = "Board Id", orderNum="1", needMerge = true)
	private Integer boardId;
	@Excel(name = "Module Id", orderNum="2", needMerge = true)
	private Integer moduleId;
	@Excel(name = "Type", orderNum="3", needMerge = true)
	private Integer type;
//	private Integer satId;
//	private Integer tpId;
	@Excel(name = "Set Pids", orderNum="4", needMerge = true)
	private String setPids;
	@Excel(name = "Program Id", orderNum="5", needMerge = true)
	private String programId;
	@Excel(name = "Audio Pids", orderNum="6", needMerge = true)
	private String audioPids;
	@Excel(name = "Subtitle Pids", orderNum="7", needMerge = true)
	private String subtitlePids;
	@Excel(name = "Out Mode", orderNum="8", needMerge = true)
	private Integer outMode;
	@Excel(name = "Out Ip", orderNum="9", needMerge = true,  width=20)
	private String outIp;
	@Excel(name = "Out Port", orderNum="10", needMerge = true)
	private int outPort;
	@Excel(name = "Out State", orderNum="11", needMerge = true)
	private Integer outState;
	@Excel(name = "Lable Id", orderNum="12", needMerge = true)
	private String lableId;
	@Excel(name = "Hls Out Mode", orderNum="13", needMerge = true,  width=15)
	private Integer hlsOutMode;
	@Excel(name = "Hls Out Ip", orderNum="14", needMerge = true,  width=20)
	private String hlsOutIp;
	@Excel(name = "Hls Out Port", orderNum="15", needMerge = true)
	private String hlsOutPort;
	
	private Programs program;
	
	private LockSignal lockSignal;
	
}
