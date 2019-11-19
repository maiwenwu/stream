package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class Satllite implements Serializable {

	private static final long serialVersionUID = 1L;

	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	
	@Excel(name = "Name", orderNum="1", needMerge = true,  width=20)
	private String name;
	
	@Excel(name = "Dir", orderNum="2", needMerge = true)
	private Integer dir;
	
	@Excel(name = "Angle", orderNum="3", needMerge = true)
	private Integer angle;
	
	@Excel(name = "Lnb Type", orderNum="4", needMerge = true)
	private String lnbType;
	
}
