package com.tech.mediaserver.net;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Bond0 {
	
	private List<String> interfaces;
	private List<String> addresses;
	private String gateway4;
	private Nameservers nameservers;
	private Parameters parameters;
	private List<Map<String, Object>> routes;
	
}
