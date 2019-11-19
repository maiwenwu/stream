package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class LockSignal implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Board Id", orderNum="0", needMerge = true)
	private Integer boardId;
	@Excel(name = "Module Id", orderNum="1", needMerge = true)
	private Integer moduleId;
	@Excel(name = "Sat Id", orderNum="2", needMerge = true)
	private Integer satId;
	@Excel(name = "Tp Id", orderNum="3", needMerge = true)
	private Integer tpId;
	@Excel(name = "Status", orderNum="4", needMerge = true)
	private Integer status;
	
}
