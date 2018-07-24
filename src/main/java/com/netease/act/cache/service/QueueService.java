package com.netease.act.cache.service;

import com.netease.act.cache.bean.EvictBO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;

@Service
@Slf4j
public class QueueService implements InitializingBean {


    public static final String ACT_CACHE_EVICT_QUEUE = "act_cache_evict_queue";

    public static final String ACT_CACHE_ACK_QUEUE = "act_cache_ack_queue";

    @Autowired
    RedissonClient mRedis;

    public BlockingDeque<EvictBO> mEvictQueue;

    public BlockingDeque<EvictBO> mAckQueue;


    public void putToEvict(EvictBO bo) {
        try {
            mEvictQueue.put(bo);
        } catch (InterruptedException e) {
            log.error("queue put error", e);
        }
    }

    public EvictBO getFromEvict() throws InterruptedException {
        try {
            return mEvictQueue.take();
        } catch (InterruptedException e) {
            log.error("queue take error", e);
            throw e;
        }
    }


    public EvictBO getFromAck() throws InterruptedException {
        try {
            return mAckQueue.take();
        } catch (InterruptedException e) {
            log.error("queue take error", e);
            throw e;
        }
    }

    public void putToAck(EvictBO bo) {
        try {
            mAckQueue.put(bo);
        } catch (InterruptedException e) {
            log.error("queue put error", e);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        mEvictQueue = mRedis.getBlockingDeque(ACT_CACHE_EVICT_QUEUE);

        mAckQueue = mRedis.getBlockingDeque(ACT_CACHE_ACK_QUEUE);
    }
}
