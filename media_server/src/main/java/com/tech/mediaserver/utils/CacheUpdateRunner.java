package com.tech.mediaserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdateRunner implements CommandLineRunner {

	@Autowired
	private RedisUtil redisUtil;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		redisUtil.remove("user");
	}

}
