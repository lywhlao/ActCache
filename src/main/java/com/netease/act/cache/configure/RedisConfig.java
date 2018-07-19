package com.netease.act.cache.configure;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.port}")
    String port;


    public static final String REDISSON_ADDR_PRE="redis://";


    @Bean
    public RedissonClient getRedisClient(){
        Config config = new Config();
        //2.没有sentinel的时候
        String address = REDISSON_ADDR_PRE+host+ ":" +port;
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }



}
