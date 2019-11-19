package com.tech.mediaserver.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.service.ConfigurationService;
import com.tech.mediaserver.service.OutputConfigService;
import com.tech.mediaserver.utils.CommandUtils;
import com.tech.mediaserver.utils.FilesUtils;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;
import com.tech.mediaserver.utils.StreamSocketApi;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
	private StreamSocketApi mStreamSocketApi = StreamSocketApi.getInstance();

	@Autowired
	private ConfigurationService configurationService;
    @Autowired
    private OutputConfigService outputConfigService;

	@RequestMapping("/exportExcel")
	public void exportExcel(HttpServletResponse response) {
		configurationService.exportExcel(response);
	}

	@RequestMapping("/importExcel")
	public int importExcel(MultipartFile file) {
		CommandUtils.execCommand(WebConstant.KILL_DAEMON_PROCESS_COMMAND);
		return configurationService.importExcel(file);
	}

	@RequestMapping("/uploadFile")
	public boolean uploadFile(MultipartFile file) {
		return configurationService.uploadFile(file);
	}

	@RequestMapping("/checkFile")
	public MessageBean checkFile(String file_name) {
		JSONObject root = new JSONObject();
		root.put("name", file_name);
		MessageBean msgBean = mStreamSocketApi.checkFile(root.toString());
		return msgBean;
	}

	@RequestMapping("/getPCState")
	public MessageBean getPCState() {
		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean msgBean = mStreamSocketApi.getPCState(root.toString());
		return msgBean;
	}

	@RequestMapping("/getModuleLog")
	public MessageBean getModuleLog(Integer board_id, Integer module_id, Integer log_level) {
		JSONObject root = new JSONObject();
		root.put("board_id", board_id);
		root.put("module_id", module_id);
		root.put("log_level", log_level);
		MessageBean msgBean = mStreamSocketApi.getModuleLog(root.toString());
		return msgBean;
	}

	@RequestMapping("/getUpdateInfo")
	public MessageBean getUpdateInfo(String list) {
		MessageBean msgBean = mStreamSocketApi.getUpdateInfo(list.toString());
		return msgBean;
	}

	@RequestMapping("/readLog")
	public JSONObject readLogs(Integer type, Integer file_pos, Integer log_level) {
		return configurationService.readLog(type, file_pos, log_level);
	}
	
	@RequestMapping("/getFileSize")
	public Long getFileSize(Integer type, Integer log_level) {
		return configurationService.getFileSize(type, log_level);
	}
	
	@RequestMapping("/getChangeLog")
	public JSONArray getChangeLog() {
		return configurationService.getChangeLog();
	}

	@RequestMapping("/immeReboot")
	public MessageBean immeReboot() {
		MessageBean msgBean = mStreamSocketApi.immeReboot();
		return msgBean;
	}

	@RequestMapping("/updateModule")
	public MessageBean updateModule(String update_list) {
		MessageBean msgBean = mStreamSocketApi.updateModule(update_list.toString());
		return msgBean;
	}

	@RequestMapping("/updatePC")
	public MessageBean updatePC() {
		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean msgBean = mStreamSocketApi.updatePC(root.toString());
		return msgBean;
	}

	@RequestMapping("/getVersion")
	public JSONObject getVersion() {
		return FilesUtils.getVersion();
	}
	
	@RequestMapping("/resetFactory")
	public MessageBean resetFactory() {
		configurationService.deleteChannelRecord();
		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean msgBean = mStreamSocketApi.resetFactory(root.toString());
		return msgBean;
	}
	
	@RequestMapping("/resetStreams")
	public MessageBean resetStreams() {
		configurationService.deleteChannelRecord();
		outputConfigService.deleteAllOutPut();
		JSONObject root = new JSONObject();
		root.put("type", 0);
		MessageBean messageBean = mStreamSocketApi.deleteAllStream(root.toString());
		return messageBean;
	}
}
