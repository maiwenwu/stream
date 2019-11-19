package com.tech.mediaserver.net;

import lombok.Data;

@Data
public class NetWorkSetting {
	
	private String ip;
	private String netmask;
	private String gateway;
	private String dns1;
	private String dns2;
	
}
