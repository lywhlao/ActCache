package com.netease.mail.actCache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class ActCacheApplicationTests {

	@Test
	public void contextLoads() {

		System.out.println(UUID.randomUUID().toString());
	}

}
