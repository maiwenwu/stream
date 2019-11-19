package com.tech.mediaserver;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.tech.mediaserver.utils.OSUtils;
import com.tech.mediaserver.utils.SigarConfig;

@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
public class MediaServerApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		// TODO Auto-generated method stub
		
		
		return application.sources(MediaServerApplication.class);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(MediaServerApplication.class, args);
		try {
			SigarConfig.initSigar();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OSUtils.getInstance();
	}
	
}
