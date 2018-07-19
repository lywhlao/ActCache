package com.netease.act.cache.configure;

import com.netease.act.cache.configure.serialize.FastJsonSerializer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    String host;

    @Value("${spring.redis.port}")
    String port;


    public static final String REDISSON_ADDR_PRE="serialize://";


    @Bean
    public RedissonClient getRedisClient(){
        Config config = new Config();
        //2.没有sentinel的时候
        String address = REDISSON_ADDR_PRE+host+ ":" +port;
        config.useSingleServer().setAddress(address);
        return Redisson.create(config);
    }


    @Bean
    public RedisTemplate<String, Object> stringStringRedisTemplate(JedisConnectionFactory jedisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(new FastJsonSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return  redisTemplate;
    }


}
