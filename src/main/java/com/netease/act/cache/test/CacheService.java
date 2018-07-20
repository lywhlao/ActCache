package com.netease.act.cache.test;

import ch.qos.logback.core.util.TimeUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {




    @Cacheable(cacheNames = "test",key ="#input")
    public String test(String input){
        return input+System.currentTimeMillis();
    }
}
