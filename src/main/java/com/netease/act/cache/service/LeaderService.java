package com.netease.act.cache.service;

import com.google.common.base.MoreObjects;
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


    @Override
    public void afterPropertiesSet() throws Exception {

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                LeaderSelector leaderSelector=new LeaderSelector(mZKService.getZKClient(), "/act_cahe_leader", new LeaderSelectorListenerAdapter() {
                    @Override
                    public void takeLeadership(CuratorFramework client) throws Exception {
                        while (true){
                            EvictBO evictBO = mQueue.get();
                            if(evictBO!=null){
                                log.info("leader get queue ==>{}",evictBO);
                            }
                        }
                    }
                });
                leaderSelector.start();
            }
        });
    }

    @PreDestroy
    public void destory(){
        MoreExecutors.shutdownAndAwaitTermination(mExecutor,20, TimeUnit.SECONDS);
    }
}
