package com.netease.act.cache.test;

import com.google.common.collect.Sets;
import com.netease.act.cache.core.ActCacheMediator;
import com.netease.act.cache.core.ActivityCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {



    @Bean
    public CacheManager testCache(){
        ActivityCacheManager manager=new ActivityCacheManager(cacheMediator());
        manager.setCacheSets(Sets.newHashSet("test"));
        return manager;
    }

    @Bean
    public ActCacheMediator cacheMediator(){
        return new ActCacheMediator();
    }

}
