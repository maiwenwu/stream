package com.tech.mediaserver.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.tech.mediaserver.utils.RedisUtil;

@Component
public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private RedisUtil redisUtil;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String user = redisUtil.getString("user");
//		User user = (User)request.getSession().getAttribute("user");
		if (user == null || "".equals(user.toString())) {
			response.sendRedirect("/");
			return false;
		} 
		
		return true;
	}
	
}
