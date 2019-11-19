package com.tech.mediaserver.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tech.mediaserver.controller.UserController;

public class WebConstant {
	
	public static final int WEB_GET_SIGNAL_INFO = 0;
	public static final int WEB_SET_SAT_TP_INFO = 1;
	public static final int WEB_REQUEST_TP_SEARCH = 2;
	public static final int WEB_REQUEST_SAT_SEARCH = 3;
	public static final int WEB_REQUEST_BLIND_SEARCH = 4;
	public static final int WEB_REQUEST_EXIT_SEARCH = 5;
	public static final int WEB_SET_OUTPUT_CONFIG = 6;
	public static final int WEB_SET_FACTORY_DEFAULT= 7;
	public static final int WEB_SET_MODULE_UPDATE = 8;


	public static final int WEB_SET_REBOOT_SYSTEM = 100;
	public static final int WEB_SET_UPLOAD =101;
	public static final int WEB_SET_SYSTEM_UPDATE =102;
	public static final int WEB_GET_SYSTEM_INFO =103; 
	public static final int WEB_GET_MODULE_LOG_FILE =104;
	public static final int WEB_GET_SEARCH_STATUS =105;
	public static final int WEB_GET_UPDATE_INFO =106;
	public static final int WEB_GET_MODULE_INFO = 107;
	public static final int WEB_GET_STREAM_INFO = 108;
	public static final int WEB_GET_ONLINE_INFO = 109;
	public static final int WEB_DELETE_ALL_STREAM = 110;
	public static final int RESTAR_STREAM_START_SHELL = 111;
	
	
	public static final int PORT = 51020;
	public static final String IP_ADDR = "127.0.0.1";
	public static final String MODEL_NAME = "web";
	public final static String TONE[] = {"On", "Off"};
	public final static String LNB_POWER[] = {"13V", "18V"};
	public final static String ASTRUIPOL[] = { "H", "V", "L", "R" };
	public final static Logger logger =  LoggerFactory.getLogger(UserController.class);
	
	
	
	public final static int MAX_PIDS = 59;
	public final static int MAZ_STREAMS = 14;
	
	public final static int TYPE_PROGRAM = 0;
	public final static int TYPE_TP = 1;
	public final static int TYPE_PID = 2;
	
	public final static String PORT_PATH = "/opt/stream_info/port.json";
	
	public final static String VERSION_CFG_PATH = "/opt/stream_info/version.conf";
	public final static String MEDIA_CONTROL_KEY = "MEDIA_CONTROL_VERSION=";
	public final static String MEDIA_CMDLINE_KEY = "MEDIA_CMDLINE_VERSION=";
	public final static String MEDIA_SERVER_KEY = "MEDIA_SERVER_VERSION=";
	public final static String MEDIA_FRONTEND_KEY = "MEDIA_FRONTEND_VERSION=";
	public final static String HLS_KEY = "HLS_VERSION=";
	
	public final static String VERSION = "version=";
	public final static String DATE = "date=";
	public final static String DESCRIBE = "describe=";
	
	public final static String NETPLAN_COMMAND = "netplan apply";
	public final static String RESTART_COMMAND = "bash /opt/stream_server/stream_start.sh";
	public final static String KILL_DAEMON_PROCESS_COMMAND = "pkill -f daemon_process.sh";
	public final static String START_DAEMON_DAEMON_PROCESS_COMMAND = "bash ./daemon_process.sh &";
	public final static String KILL_STREAM_SERVER_COMMAND = "pkill stream_server";
	
	public final static String NET_INGERFACE = "bond0";//"bond0"; //enp0s31f6 //enp2s0
	
	public final static String NET_FILE_PATH = "/etc/netplan/01-netcfg.yaml";
	public final static String HLS_DB_PATH = "/mediadisk/hls/mdb";
	
	public final static String CHANGE_TAG_PATH = "/opt/stream_server/VersionLog.conf";
}
