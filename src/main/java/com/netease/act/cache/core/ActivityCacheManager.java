package com.netease.act.cache.core;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.netease.act.cache.bean.CacheContext;
import com.netease.act.cache.util.ExpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hzlaojiaqi on 2017/11/28.
 */
@Slf4j
public class ActivityCacheManager extends AbstractCacheManager {


    private ActCacheMediator mediator;

    private Set<String> cacheSets;


    public ActivityCacheManager(ActCacheMediator mediator) {
        this.mediator = mediator;
    }

    public void setCacheSets(Set<String> cacheNames) {
        ExpUtil.check(cacheNames != null);
        this.cacheSets = cacheNames;
    }

    public void setMediator(ActCacheMediator mediator) {
        this.mediator = mediator;
    }


    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        ExpUtil.check(mediator != null);
        this.mediator.setCacheManager(this);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        ExpUtil.check(mediator != null && cacheSets != null);

        Iterator<String> iterator = cacheSets.iterator();
        CacheContext cacheContext = mediator.getCacheBuilder();
        Set<ActCache> actCacheSet = new HashSet<ActCache>();
        while (iterator.hasNext()) {
            ActCache actCache = createActCache(iterator, cacheContext);
            actCacheSet.add(actCache);
        }
        return actCacheSet;
    }

    public ActCache getCacheByName(String cacheName) {
        Cache cache = getCache(cacheName);
        //todo why is false?
//        ExpUtil.check(ActCache.class.getClass().isInstance(cache));
        return (ActCache) cache;
    }

    private ActCache createActCache(Iterator<String> iterator, CacheContext cacheContext) {
        String name = iterator.next();
        CacheLoader<Object, Object> cacheLoad = mediator.getCacheLoad(name);
        LoadingCache<Object, Object> loadingCache = cacheContext.getCacheBuilder().build(cacheLoad);
        return new ActCache(name, mediator, loadingCache);
    }

}
