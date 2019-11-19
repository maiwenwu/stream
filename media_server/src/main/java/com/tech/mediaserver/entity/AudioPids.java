package com.tech.mediaserver.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class AudioPids implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Excel(name = "Id", orderNum="0", needMerge = true)
	private Integer id;
	@Excel(name = "Board Id", orderNum="1", needMerge = true)
	private Integer boardId;
	@Excel(name = "Module Id", orderNum="2", needMerge = true)
	private Integer moduleId;
	@Excel(name = "Program Id", orderNum="3", needMerge = true)
	private Integer programId;
	@Excel(name = "Audio Pid", orderNum="4", needMerge = true)
	private Integer audioPid;
	@Excel(name = "Language", orderNum="5", needMerge = true)
	private Integer language;
	@Excel(name = "Type", orderNum="6", needMerge = true)
	private Integer type;
	@Excel(name = "mModify Pid", orderNum="7", needMerge = true , width=15)
	private Integer modifyPid;
	
	private Programs program;
	
}
