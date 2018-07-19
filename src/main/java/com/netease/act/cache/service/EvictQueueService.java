package com.netease.act.cache.service;

import com.netease.act.cache.bean.EvictBO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class EvictQueueService implements InitializingBean {


    public static final String ACT_CACHE_EVICT_QUEUE = "act_cache_evict_queue";
    @Autowired
    RedissonClient mRedis;

    public BlockingDeque<EvictBO> mQueue;


    public void put(EvictBO bo) {
        try {
            mQueue.put(bo);
        } catch (InterruptedException e) {
            log.error("queue put error", e);
        }
    }

    public EvictBO get() {
        try {
            return mQueue.take();
        } catch (InterruptedException e) {
            log.error("queue take error", e);
        }
        return null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        mQueue = mRedis.getBlockingDeque(ACT_CACHE_EVICT_QUEUE);

    }
}
