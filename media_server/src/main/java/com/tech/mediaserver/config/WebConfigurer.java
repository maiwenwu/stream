package com.tech.mediaserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tech.mediaserver.interceptor.LoginInterceptor;

@Configuration
public class WebConfigurer implements WebMvcConfigurer{

	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		
		InterceptorRegistration loginRegistry = registry.addInterceptor(loginInterceptor);
		
		//拦截路径
		loginRegistry.addPathPatterns("/**");
		
		//排除拦截
		loginRegistry.excludePathPatterns("/");
		loginRegistry.excludePathPatterns("/cdn_heartbeat");
		loginRegistry.excludePathPatterns("/login");
		loginRegistry.excludePathPatterns("/user/loginUser");
		
		//排除资源拦截
		loginRegistry.excludePathPatterns("/css/**");
		loginRegistry.excludePathPatterns("/images/**");
		loginRegistry.excludePathPatterns("/js/**");
		loginRegistry.excludePathPatterns("/vendor/**");
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
