package com.tech.mediaserver.net;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Eno4 {
	
	private List<String> addresses;
	private String gateway4;
	private Nameservers nameservers;
	private List<Map<String, Object>> routes;
	
}
