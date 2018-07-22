package com.netease.act.cache.service;

import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.constant.RedisKey;
import com.netease.act.cache.core.ActCacheMediator;
import com.netease.act.cache.core.ActivityCacheManager;
import com.netease.act.cache.util.ExpUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class PublishService {

    @Autowired
    RedissonClient mRClient;

    @Autowired
    ActCacheMediator mMediator;

    private RTopic<EvictBO> mEvictTopic;

    private RTopic<EvictBO> mAckTopic;

    @PostConstruct
    public void init() {
//        mAckTopic = mRClient.getTopic(RedisKey.ACK_EVICT_TOPIC);
        mEvictTopic = mRClient.getTopic(RedisKey.BROADCAST_EVICT_TOPIC);
        mEvictTopic.addListener(new MessageListener<EvictBO>() {
            @Override
            public void onMessage(String channel, EvictBO msg) {
                log.info("get broadcast ==>{}", msg);
                mMediator.evictByCacheName(msg.getCacheName(),msg.getKey());
            }
        });
    }

    public void sendBroadcast(EvictBO evictBO) {
        ExpUtil.check(evictBO != null);
        mEvictTopic.publish(evictBO);
    }

    /**
     * //todo ack
     */
    public void registerAckTopic() {
        mAckTopic.addListener(new MessageListener<EvictBO>() {
            @Override
            public void onMessage(String s, EvictBO evictBO) {

            }
        });
    }


}