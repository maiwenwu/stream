package com.tech.mediaserver.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class RebootInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer enable;
	private Integer type;
	private Integer year;
	private Integer month;
	private Integer monthDay;
	private Integer weekDay;
	private Integer hour;
	private Integer minute;
	
}
