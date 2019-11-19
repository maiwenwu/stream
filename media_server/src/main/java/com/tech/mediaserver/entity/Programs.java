package com.tech.mediaserver.entity;

import java.io.Serializable;
import java.util.List;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Programs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	@Excel(name = "Board Id", orderNum="1", needMerge = true)
	private Integer boardId;
	@Excel(name = "Module Id", orderNum="2", needMerge = true)
	private Integer moduleId;
	@Excel(name = "Tp Id", orderNum="3", needMerge = true)
	private Integer tpId;
	@Excel(name = "Service Name", orderNum="4", needMerge = true,  width=30)
	private String serviceName;
	@Excel(name = "Pmt Pid", orderNum="5", needMerge = true)
	private Integer pmtPid;
	@Excel(name = "Service Id", orderNum="6", needMerge = true)
	private Integer serviceId;
	@Excel(name = "Pcr Pid", orderNum="7", needMerge = true)
	private Integer pcrPid;
	@Excel(name = "Vid Pid", orderNum="8", needMerge = true)
	private Integer vidPid;
	@Excel(name = "Ecm Pids", orderNum="9", needMerge = true)
	private String ecmPids;
	@Excel(name = "Ca System Ids", orderNum="10", needMerge = true)
	private String caSystemIds;
	@Excel(name = "Tv Type", orderNum="11", needMerge = true)
	private Integer tvType;
	@Excel(name = "Ca Type", orderNum="12", needMerge = true)
	private Integer caType;
	@Excel(name = "Video Modify Pid", orderNum="13", needMerge = true,  width=20)
	private Integer videoModifyPid;
	
	private List<SubtitlePids> subtitlePids;
	
}
