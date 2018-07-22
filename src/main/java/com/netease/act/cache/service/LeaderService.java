package com.netease.act.cache.service;

import com.google.common.util.concurrent.MoreExecutors;
import com.netease.act.cache.bean.EvictBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LeaderService implements InitializingBean{


    ExecutorService mExecutor= Executors.newSingleThreadExecutor();

    @Autowired
    ZKService mZKService;

    @Autowired
    EvictQueueService mQueue;

    @Autowired
    PublishService mPublish;


    @Override
    public void afterPropertiesSet() throws Exception {

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doLeaderWork();
                } catch (InterruptedException e) {
                  log.error("leader interrupted ",e);
                }
            }
        });
    }


    private void doLeaderWork() throws InterruptedException {
        LeaderSelector leaderSelector=new LeaderSelector(mZKService.getZKClient(), "/act_cache_leader", new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
//                // register ack
//                mPublish.registerAckTopic();

                // broadcast to client
                getMessageFromEvictQueue();
            }
        });
        leaderSelector.start();
    }

    private void getMessageFromEvictQueue() throws InterruptedException {
        while (true){
            EvictBO evictBO = mQueue.get();
            if(evictBO!=null){
                log.info("leader get evict queue ==>{}",evictBO);
                mPublish.sendBroadcast(evictBO);
            }
        }
    }

    @PreDestroy
    public void destroy(){
        MoreExecutors.shutdownAndAwaitTermination(mExecutor,20, TimeUnit.SECONDS);
    }
}
