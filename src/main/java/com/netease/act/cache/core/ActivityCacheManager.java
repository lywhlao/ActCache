package com.netease.act.cache.core;

import com.netease.act.cache.util.ExpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.HashSet;
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

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<ActCache> actCacheSet = new HashSet<ActCache>();
        actCacheSet.add(new ActCache("actCache", mediator));
        return actCacheSet;
    }

}
