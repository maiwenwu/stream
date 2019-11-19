package com.tech.mediaserver.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class IPMIUtils {
	
	public static IPMIUtils instance = null;
	
	public static String IPMI_CMD = "/opt/IPMICFG_1.30.0_build.190710/Linux/64bit/IPMICFG-Linux.x86_64";
	public static String IPMI_KEY_IP = "-m";
	public static String IPMI_KEY_GATEWAY = "-g";
	public static String IPMI_KEY_NETMASK = "-k";
			
	private IPMIUtils() {
	}
	
	public static IPMIUtils getInstance() {
		if (instance == null) { 
			synchronized (IPMIUtils.class) { 
				if (instance == null) { 
					instance = new IPMIUtils(); 
					} 
				} 
			} 
		return instance;
	}
	
	public int saveIPMICconfig(IPMISetting getIPMIConfig) {
		String set_ip_key = IPMI_KEY_IP + " " + getIPMIConfig.getIp();
		String set_gateway_key = IPMI_KEY_GATEWAY + " " + getIPMIConfig.getGateway();
		String set_netmask_key = IPMI_KEY_NETMASK + " " + getIPMIConfig.getNetmask();
		
		int result = 0;
		
		result = setConfig(set_ip_key);
		result = setConfig(set_gateway_key);
		result = setConfig(set_netmask_key);
		
//		CommandUtils.execCommand(WebConstant.KILL_STREAM_SERVER_COMMAND);
		
		return result;
	}
	
	public IPMISetting getIPMIConfig() {
		IPMISetting config = new IPMISetting();
		Map<String,String> map_ip = getConfig(IPMI_KEY_IP);
		Map<String,String> map_gateway = getConfig(IPMI_KEY_GATEWAY);
		Map<String,String> map_netmask = getConfig(IPMI_KEY_NETMASK);
		
		config.setIp(map_ip.get("IP"));
		config.setGateway(map_gateway.get("Gateway"));
		config.setNetmask(map_netmask.get("Subnet Mask"));
		return config;
	}
	
	private int setConfig(String key) {
		int result = 1;
		try {
			File file = new File(IPMI_CMD);
			if (file.exists()) {
				Process p = Runtime.getRuntime().exec(IPMI_CMD + " " + key);
				p.waitFor();
				if (p.exitValue() != 0) { //执行失败
					result = 0;
				}
			} else {
				result = 0;
			}
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private Map<String,String> getConfig(String key){
		Map<String,String> config = new HashMap<String,String>();
	    Process process = null;
	    BufferedReader in = null;
	    
	    File file = new File(IPMI_CMD);
	    if (file.exists()) {
	    	try {
				process = Runtime.getRuntime().exec(IPMI_CMD + " " + key);
			    in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    String line = "";
				while((line = in.readLine()) != null){
					String temp[] = line.split("=");
					config.put(temp[0], temp[1]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        try {
					process.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	    return config;
	}
}
