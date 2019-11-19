package com.tech.mediaserver.utils;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONObject;

/**
 * Create Time：2019年6月13日 下午2:25:46
 * Project Name：cdn-manager
 * @author Gavin
 * @version 1.0
 * @since JDK 1.8.0_162
 * File Name: SendRequestTask.java
 * Class Description: 
 */
public class SendRequestTask implements Callable<String> {
	
	private String url;
	private JSONObject parameObject;
	
	public SendRequestTask(String url, JSONObject parameObject) {
		this.url = url;
		this.parameObject = parameObject;
	}

	@Override
	public String call() throws Exception {
		String responseResult = null;
		try {
			responseResult = HttpClient.sendPostRequest(url, parameObject);
		} catch (IOException e) {
		}
		return responseResult;
	}

}
