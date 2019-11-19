package com.tech.mediaserver.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.tech.mediaserver.constant.WebConstant;
/**
 * Create Time：2018年10月16日 下午2:16:20
 * Project Name：cdn-manager
 * @author Gavin
 * @version 1.0
 * @since JDK 1.8.0_162
 * File Name: HttpClient.java
 * Class Description: HTTP远程调用工具
 */
public class HttpClient {
	
	/**
	 * post请求传输json数据
	 */
	public static String sendPostRequest(String url, JSONObject json) throws IOException {
		String result = null;
		WebConstant.logger.error("CDN post method, url is {}, send param is {}", url, json.toString());
		
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Connection", "Close");
		// 设置参数到请求对象中
		StringEntity stringEntity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
		stringEntity.setContentEncoding("utf-8");
		httpPost.setEntity(stringEntity);
		
		// 执行请求操作，并拿到结果（同步阻塞）
	    CloseableHttpResponse response = httpClient.execute(httpPost);
	    // 判断网络连接状态码是否正常(0--200都数正常)
	    HttpEntity entity = response.getEntity();
	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	        result = EntityUtils.toString(entity, "utf-8");
	    }
	    EntityUtils.consume(entity);
	    // 释放链接
	    response.close();
		
	    WebConstant.logger.error("CDN request success, response body is {}", result);
		return result;
	}
	
}
