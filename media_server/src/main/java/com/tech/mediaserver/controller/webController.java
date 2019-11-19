package com.tech.mediaserver.controller;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class webController {

	@RequestMapping(value = {"/login","/"})
	public String login() {
		return "login";
	}
	
	@RequestMapping("/system_info")
	public String systemInfo() {
		return "system_info";
	}
	
	@RequestMapping("/rf_setting")
	public String rfSetting() {
		return "rf_setting";
	}
	
	@RequestMapping("/search")
	public String search() {
		return "search";
	}
	
	@RequestMapping("/stream_manager")
	public String streamManger() {
		return "stream_manager";
	}

	
	@RequestMapping("/add_stream")
	public String addStream(Integer id, Integer board_id, Integer module_id, Model model) {
		model.addAttribute("id", id);
		model.addAttribute("board_id", board_id);
		model.addAttribute("module_id", module_id);
		return "add_stream";
	}
	
	@RequestMapping("/add_sat")
	public String addSatllite() {
		return "add_satllite";
	}
	
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
	
	@RequestMapping("/satlliteManager") 
	public String satlliteManager() {
		return "satllite_manager";
	}
	
	@RequestMapping("/configuration")
	public String configuration() {
		return "configuration";
	}
	
	@RequestMapping("/transponderManager")
	public String transponderManager() {
		return "transponder_manager";
	}
	
	@RequestMapping("/factory")
	public String factory() {
		return "factory";
	}
	
	@RequestMapping("//version_log")
	public String versionLog() {
		return "version_log";
	}
	
	@RequestMapping("/update")
	public String update() {
		return "update";
	}
	
	@RequestMapping("/other")
	public String other() {
		return "other";
	}
	
	@RequestMapping("/programManager")
	public String programManager() {
		return "program_manager";
	}
	
	@RequestMapping("/netWork")
	public String netWork() {
		return "net_work";
	}
	
	@RequestMapping("/reboot")
	public String reboot() {
		return "reboot";
	}
	
	@RequestMapping("/log")
	public String log() {
		return "log";
	}
	
	@RequestMapping("/module_log")
	public String moduleLog(Integer board_id, Integer module_id, Model model) {
		model.addAttribute("board_id", board_id+1);
		model.addAttribute("module_id", module_id+1);
		return "module_log";
	}
	
	@RequestMapping("/link")
	public void link(HttpServletResponse response) {
		 try {
	            response.sendRedirect("http://www.baidu.com");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
	@RequestMapping("/system_setting")
	public String systemSetting() {
		return "system_settings";
	}
	
	@RequestMapping("/debug")
	public String deleteStreams() {
		return "stream_list";
	}
	
	@RequestMapping("/hls_manager")
	public String hlsManager() {
		return "hls_manager";
	}
	
	@RequestMapping("/debug_update")
	public String debugUpdate() {
		return "debug_update";
	}
}
