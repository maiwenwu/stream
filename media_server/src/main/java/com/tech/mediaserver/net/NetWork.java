package com.tech.mediaserver.net;

import lombok.Data;

@Data
public class NetWork {
	
	private String version;
	private String renderer;
	private Ethernets ethernets;
	private Bonds bonds;
	
}
