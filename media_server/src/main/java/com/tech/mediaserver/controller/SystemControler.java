package com.tech.mediaserver.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.net.IPMISetting;
import com.tech.mediaserver.net.IPMIUtils;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.entity.RebootInfo;
import com.tech.mediaserver.entity.RespBean;
import com.tech.mediaserver.net.NetWorkSetting;
import com.tech.mediaserver.net.YamlUtils;
import com.tech.mediaserver.service.SystemInfoService;
import com.tech.mediaserver.utils.CommandUtils;
import com.tech.mediaserver.utils.OSUtils;
import com.tech.mediaserver.utils.StreamSocketApi;
import com.tech.mediaserver.utils.TimeZoneUtils;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;

@RestController
@RequestMapping("/os")
public class SystemControler {
	
	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();
	
	private YamlUtils yamlUtils = YamlUtils.getInstance();
	private IPMIUtils ipmiUtils = IPMIUtils.getInstance();
	private TimeZoneUtils timeUtils = TimeZoneUtils.getInstance();
	
	@Autowired
	private SystemInfoService systemInfoService;
	
	@RequestMapping("/getSystemInfo")
	public JSONObject getSystemInfo() {
		OSUtils osUtils = OSUtils.getInstance();
		JSONObject object = new JSONObject();
		Map<String, Object> memoryMap = osUtils.memoryUsage();
		
		int  cpuUsage = (int) osUtils.cpuUsage();
		object.put("cpuused", cpuUsage);
		object.put("cpufree", 100 - cpuUsage);
        long memTotal = Long.parseLong(memoryMap.get("MemTotal").toString());
        long memFree = Long.parseLong(memoryMap.get("MemFree").toString());
        long memBuffer = Long.parseLong(memoryMap.get("Buffers").toString());
        long memCache = Long.parseLong(memoryMap.get("Cached").toString());
        
        object.put("rx_data", OSUtils.getInstance().getRx_list());
        object.put("tx_data", OSUtils.getInstance().getTx_list());
        
        object.put("memused", (memTotal-memFree-memBuffer-memCache)/1024);
        object.put("memfree", memFree/1024);
        object.put("membuffer",(memBuffer+memCache)/1024);
        
		return object;
	}
	
	@RequestMapping("/getOsInfo")
	public JSONObject getOsInfo() {
		JSONObject object = new JSONObject();
		OSUtils osUtils = OSUtils.getInstance();
		Map<String, Object> osMap = osUtils.system_info();
		for(Entry<String, Object> entry : osMap.entrySet()){
		    String mapKey = entry.getKey();
		    Object mapValue = entry.getValue();
		    object.put(mapKey, mapValue);
		}
		return object;
	}
	
	@RequestMapping("/getOsUptime_Temp")
	public JSONObject getOsUptime_Temp() {
		JSONObject object = new JSONObject();
		OSUtils osUtils = OSUtils.getInstance();
		Map<String, Object> uptime = osUtils.system_uptime();
		Map<String, Object> temp = osUtils.system_temp();
		for(Entry<String, Object> entry : uptime.entrySet()){
		    String mapKey = entry.getKey();
		    Object mapValue = entry.getValue();
		    object.put(mapKey, mapValue);
		}
		
		for(Entry<String, Object> entry : temp.entrySet()){
		    String mapKey = entry.getKey();
		    Object mapValue = entry.getValue();
		    object.put(mapKey, mapValue);
		}
		return object;
	}
	
	@RequestMapping("/getNetInfo")
	public JSONObject getNetInfo() {
		
		JSONObject object = new JSONObject();
		NetWorkSetting nms_info = yamlUtils.ReadNmsInterface();
		NetWorkSetting data_info = yamlUtils.ReadDataInterface();
		IPMISetting ipmi_info = ipmiUtils.getIPMIConfig();
		object.put("nms_info", nms_info);
		object.put("data_info", data_info);
		object.put("ipmi_info", ipmi_info);
		return object;
	}

	@RequestMapping("/saveIpmiInfo")
	public RespBean saveIpmiInfo(@RequestBody IPMISetting ipmi_info) {
		return RespBean.ok("", ipmiUtils.saveIPMICconfig(ipmi_info));
	}
	
	@RequestMapping("/saveNetWorkInfo")
	public RespBean saveNetWorkInfo(@RequestBody List<NetWorkSetting> netWorkSettings) {
		
		NetWorkSetting get_data_info = yamlUtils.ReadDataInterface();
		
		NetWorkSetting nms_info = netWorkSettings.get(0);
		NetWorkSetting data_info = netWorkSettings.get(1);
		int nms_result = yamlUtils.WriteNmsInterface(nms_info);
		int data_result = yamlUtils.WriteDataInterface(data_info);
		
		if (get_data_info.getIp().equals(data_info.getIp()) && get_data_info.getNetmask().equals(data_info.getNetmask()) && 
				get_data_info.getGateway().equals(data_info.getGateway()) && get_data_info.getDns1().equals(data_info.getDns1())) {
			
		} else {
			CommandUtils.execCommand(WebConstant.KILL_STREAM_SERVER_COMMAND);
		}
		
		return RespBean.ok("", (nms_result & data_result));
		
	}
	
	@RequestMapping("/execNetplanApply")
	public RespBean execNetplanApply() {
		return RespBean.ok("", CommandUtils.execCommand(WebConstant.NETPLAN_COMMAND));
	}
	
	@RequestMapping("/execRestartProgram")
	public MessageBean execRestartProgram() {

		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean msgBean = mStreamSocketApi.restartStartShell(root.toString());
		
		return msgBean;
	}
	
	@RequestMapping("/getTimeZoneList")
	public JSONObject getTimeZoneList() {
		JSONObject object = new JSONObject();
		object.put("zone_list", timeUtils.getmZoneList());
		object.put("time_zone", timeUtils.getDefaultTimeZone());
		object.put("date_time", timeUtils.getTimeByZoneId(timeUtils.getDefaultTimeZone()));
		return object;
	}
	
	@RequestMapping("/getTimeByZoneId")
	public JSONObject getTimeByZoneId(int zone_index) {
		JSONObject root = new JSONObject();
		root.put("date_time", timeUtils.getTimeByZoneId(zone_index));
		return root;
	}
	
	@RequestMapping("/saveTimeZone")
	public RespBean saveTimeZone(int zone_index,String date_time) {
		timeUtils.setTimeZone(zone_index);
		timeUtils.setDateTime(date_time);
		return RespBean.ok("", 1);
	}
		
	@RequestMapping("/getRebootInfo")
	public List<RebootInfo> getRebootInfo() {
		return systemInfoService.getRebootInfo();
	}
	
	@RequestMapping("/deleteRebootInfo")
	public RespBean deleteRebootInfo() {
		return RespBean.ok("", systemInfoService.deleteRebootInfo());
	}
	
	@RequestMapping("/addRebootInfo")
	public RespBean addRebootInfo(@RequestBody List<RebootInfo> rebootInfos) {
		return RespBean.ok("Successful!", systemInfoService.addRebootInfo(rebootInfos));
	}

}

