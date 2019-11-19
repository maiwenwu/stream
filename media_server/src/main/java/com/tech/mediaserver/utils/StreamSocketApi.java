package com.tech.mediaserver.utils;

import java.net.SocketException;

import com.tech.mediaserver.constant.WebConstant;
import com.tech.mediaserver.utils.StreamSocket.MessageBean;

public class StreamSocketApi {
	
	private static StreamSocket streamSocket;
	public static StreamSocketApi instance = null;
	private StreamSocketApi() {
		try {
			streamSocket = new StreamSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static StreamSocketApi getInstance() {
		if (instance == null) { 
			synchronized (StreamSocketApi.class) { 
				if (instance == null) { 
					instance = new StreamSocketApi(); 
					} 
				} 
			} 
		return instance;
	}
	
	/**
	 * api stream
	 */
	
	public MessageBean singleStartStreaming(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_OUTPUT_CONFIG, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}

	public MessageBean singleStopStreaming(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_OUTPUT_CONFIG, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean stopAllStreaming(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_OUTPUT_CONFIG, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean startStreaming(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_OUTPUT_CONFIG, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR,WebConstant. PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean getModuleInfo(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_MODULE_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR,WebConstant. PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean getStreamInfo(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_STREAM_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR,WebConstant. PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean deleteAllStream(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_DELETE_ALL_STREAM, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR,WebConstant. PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	/**
	 * api factory
	 */
	
	public MessageBean resetFactory(String json_string) {
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_FACTORY_DEFAULT, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	/**
	 * api search 
	 */
	
	public MessageBean tpSearch(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_REQUEST_TP_SEARCH, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean satSearch(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_REQUEST_SAT_SEARCH, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean blindSearch(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_REQUEST_BLIND_SEARCH, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean lockSignal(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_SAT_TP_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean getHeartInfo(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_SIGNAL_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean exitSearch(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_REQUEST_EXIT_SEARCH, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean getSearchStatus(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_SEARCH_STATUS, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	
	/**
	 * api update
	 */
	public MessageBean checkFile(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_UPLOAD, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean updateModule(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_MODULE_UPDATE, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean updatePC(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_SYSTEM_UPDATE, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean getUpdateInfo(String json_string)
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_UPDATE_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	
	/**
	 * api system
	 */
	public MessageBean getPCState(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_SYSTEM_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	public MessageBean restartStartShell(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.RESTAR_STREAM_START_SHELL, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	
	/**
	 * api log
	 */
	public MessageBean getModuleLog(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_MODULE_LOG_FILE, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	/**
	 * api reboot
	 */
	public MessageBean immeReboot() 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_SET_REBOOT_SYSTEM, "");
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	/**
	 * api online
	 */
	public MessageBean getOnlineInfo(String json_string) 
	{
		String data = streamSocket.packCommand(WebConstant.MODEL_NAME, WebConstant.WEB_GET_ONLINE_INFO, json_string);
		streamSocket.sendCommand(WebConstant.IP_ADDR, WebConstant.PORT, data);
		MessageBean msgBean = streamSocket.receiveMessageSync();
		return msgBean;
	}
	
	
}
