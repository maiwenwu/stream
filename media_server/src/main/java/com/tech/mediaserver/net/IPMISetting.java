package com.tech.mediaserver.net;

import lombok.Data;

@Data
public class IPMISetting {
	
	private String ip;
	private String netmask;
	private String gateway;
	private String dns;
	
}
