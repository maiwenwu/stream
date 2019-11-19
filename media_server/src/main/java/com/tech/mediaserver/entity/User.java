package com.tech.mediaserver.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer role;
	private String userName;
	private String password;
	
}
