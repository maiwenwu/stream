package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Rf implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Board Id", orderNum="0", needMerge = true)
	private Integer boardId;
	
	@Excel(name = "Lnb Power", orderNum="1", needMerge = true)
	private Integer lnbPower;
	
	@Excel(name = "Tone", orderNum="2", needMerge = true)
	private Integer tone;
	
	@Excel(name = "Diseqc1_0", orderNum="3", needMerge = true)
	private Integer diseqc1_0;
	
	@Excel(name = "Diseqc1_1", orderNum="4", needMerge = true)
	private Integer diseqc1_1;
	
}
