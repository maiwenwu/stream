package com.tech.mediaserver.constant;
/**
 * Create Time：2019年6月24日 下午1:56:08
 * Project Name：cdn-manager
 * @author Gavin
 * @version 1.0
 * @since JDK 1.8.0_162
 * File Name: InterfaceConstant.java
 * Class Description: 后台接口默认值
 */
public interface InterfaceConstant {

	String HTTP = "http://";
	String COLON = ":";
	
	// 直播录制
	String HLS_PORT = "8016";
	String HLS_REGIST = "/live_regist";
	String HLS_UNREGIST = "/live_unregist";
	String HLS_SYNC = "/live_sync";
	String HLS_MODIFY = "/live_modify";
	String HLS_AUTH = "/live_auth";
	
}
