package com.netease.act.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ActCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActCacheApplication.class, args);
	}
}
