package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Transponder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	@Excel(name = "Sat Id", orderNum="1", needMerge = true)
	private Integer satId;
	@Excel(name = "Freq", orderNum="2", needMerge = true)
	private Integer freq;
	@Excel(name = "SymbolRate", orderNum="3", needMerge = true)
	private Integer symbolRate;
	@Excel(name = "Polarization", orderNum="4", needMerge = true)
	private Integer polarization;
	@Excel(name = "Fec", orderNum="5", needMerge = true)
	private Integer fec;
	@Excel(name = "Ts Id", orderNum="6", needMerge = true)
	private Integer tsId;
	@Excel(name = "On Id", orderNum="7", needMerge = true)
	private Integer onId;
	@Excel(name = "Eit Id", orderNum="8", needMerge = true)
	private Integer eitId;
	@Excel(name = "emm Pids", orderNum="9", needMerge = true)
	private String emmPids;
	@Excel(name = "Ca System Ids", orderNum="10", needMerge = true)
	private String caSystemIds;
	
}
