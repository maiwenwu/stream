package com.tech.mediaserver.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class OutPort {

	@Excel(name = "Board Id", orderNum="0", needMerge = true)
	public Integer boardId;
	@Excel(name = "Module_id", orderNum="1", needMerge = true)
	public Integer moduleId;
	@Excel(name = "port", orderNum="2", needMerge = true)
	public Integer port;
	
}
