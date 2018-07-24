package com.netease.act.cache.service;

import ch.qos.logback.core.util.TimeUtil;
import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.constant.RedisKey;
import com.netease.act.cache.core.ActCacheMediator;
import com.netease.act.cache.util.ExpUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
@Slf4j
public class PublishService {

    @Autowired
    RedissonClient mRClient;

    @Autowired
    ActCacheMediator mMediator;

    @Autowired
    QueueService mQueue;

    private RTopic<EvictBO> mEvictTopic;

    @PostConstruct
    public void init() {
        mEvictTopic = mRClient.getTopic(RedisKey.BROADCAST_EVICT_TOPIC);
        mEvictTopic.addListener(new MessageListener<EvictBO>() {
            @Override
            public void onMessage(String channel, EvictBO msg) {
                msg.setIp("127.0.0.1==>"+System.currentTimeMillis());
                mQueue.putToAck(msg);
                log.info("client send ack msg==>:{}",msg);
            }
        });
    }

    public void sendBroadcast(EvictBO evictBO) {
        ExpUtil.check(evictBO != null);
        mEvictTopic.publish(evictBO);
    }

}