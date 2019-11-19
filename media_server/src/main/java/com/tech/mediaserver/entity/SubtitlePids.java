package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class SubtitlePids implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	@Excel(name = "Board Id", orderNum="1", needMerge = true)
	private Integer boardId;
	@Excel(name = "Module Id", orderNum="2", needMerge = true)
	private Integer moduleId;
	@Excel(name = "Program Id", orderNum="3", needMerge = true)
	private Integer programId;
	@Excel(name = "Sub Pid", orderNum="4", needMerge = true)
	private Integer subPid;
	@Excel(name = "Language", orderNum="5", needMerge = true)
	private Integer language;
	@Excel(name = "Type", orderNum="6", needMerge = true)
	private Integer type;
	@Excel(name = "CompositionPageId", orderNum="7", needMerge = true)
	private Integer compositionPageId;
	@Excel(name = "AncililaryPageId", orderNum="8", needMerge = true)
	private Integer ancililaryPageId;
	@Excel(name = "Modify Pid", orderNum="9", needMerge = true)
	private Integer modifyPid;
	
}
