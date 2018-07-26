package com.netease.act.cache.service;

import com.google.common.util.concurrent.MoreExecutors;
import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.constant.Constant;
import com.netease.act.cache.core.ActCacheMediator;
import com.netease.act.cache.util.thread.CustomThreadFactory;
import com.netease.act.cache.util.thread.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class LeaderService implements InitializingBean,Constant{


    ExecutorService mExecutor = Executors.newSingleThreadExecutor(CustomThreadFactory.getInstance());

    ExecutorService mAckExecutor = Executors.newSingleThreadExecutor(CustomThreadFactory.getInstance());


    @Autowired
    ZKService mZKService;

    @Autowired
    QueueService mQueue;

    @Autowired
    PublishService mPublish;

    @Autowired
    DiscoverService mDiscoverService;

    @Autowired
    ActCacheMediator mMediator;

    volatile LeaderSelector mLeaderShip;

    Map<EvictBO, AtomicInteger> mAckMap = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        initLeaderShip();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doBroadcastWork();
                } catch (InterruptedException e) {
                    log.error("leader interrupted ", e);
                }
            }
        });
        mAckExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(mLeaderShip.hasLeadership()){
                        doLeaderAckWork();
                    }else{
                        idle();
                    }
                }
            }
        });
    }


    /**
     * recheck count map if client removed
     */
    public void recheckCountMap(){
        if(!isLeader()){
            return;
        }
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int clientSize = mDiscoverService.getClientSize();
                mAckMap.forEach((k,v)->{
                    compareAndEvict(k,v.get(),clientSize);
                });
            }
        });
    }



    /**
     * if all client ack, do evict caches(guava and redis) operation
     */
    private void doLeaderAckWork() {
        try {
            EvictBO fromAck = mQueue.getFromAck();
            if (fromAck != null) {
                AtomicInteger atomicInteger = mAckMap.putIfAbsent(fromAck, new AtomicInteger(1));
                int currentValue = 1;
                if (atomicInteger != null) {
                    currentValue = atomicInteger.incrementAndGet();
                }
                int clientSize = mDiscoverService.getClientSize();
                log.info("leader get ack==> current:{},expect:{}", currentValue, clientSize);
                compareAndEvict(fromAck, currentValue, clientSize);
            }
        } catch (InterruptedException e) {
            log.error("ack interrupted ", e);
        }
    }

    /**
     *  is not leader ,just sleep
     */
    private void idle() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
        }
    }

    /**
     *  recheck if can evict caches
     * @param fromAck
     * @param currentValue
     * @param clientSize
     */
    private void compareAndEvict(EvictBO fromAck, int currentValue, int clientSize) {
        if (currentValue >= clientSize) {
            log.info("ack success get from queue ==>{}", fromAck);
            fromAck.setPhase(Constant.PHASE_COMMIT);
            mPublish.sendBroadcast(fromAck);
            mAckMap.remove(fromAck);
        }else{
            log.info("ack not success  current:{},client:{}",currentValue,clientSize);
        }
    }


    /**
     * @throws InterruptedException
     */
    private void doBroadcastWork() throws InterruptedException {
        mLeaderShip.start();
    }

    /**
     * @throws InterruptedException
     */
    private void getMessageFromEvictQueue() throws InterruptedException {
        while (true) {
            EvictBO evictBO = mQueue.getFromEvict();
            if (evictBO != null) {
                log.info("leader get evict queue ==>{}", evictBO);
                mPublish.sendBroadcast(evictBO);
            }
        }
    }

    public boolean isLeader(){
        return mLeaderShip!=null && mLeaderShip.hasLeadership();
    }

    /**
    *  init leader ship
     * @return
     */
    private LeaderSelector initLeaderShip(){
        if(mLeaderShip==null){
            mLeaderShip = new LeaderSelector(mZKService.getZKClient(), ACT_CACHE_LEADER, new LeaderSelectorListenerAdapter() {
                @Override
                public void takeLeadership(CuratorFramework client) throws Exception {
                    log.info("leader service,  inner is leader==>{}", mLeaderShip.hasLeadership());
                    // broadcast to client
                    getMessageFromEvictQueue();
                }
            });
        }
        return mLeaderShip;
    }

    @PreDestroy
    public void destroy() {
        MoreExecutors.shutdownAndAwaitTermination(mExecutor, 20, TimeUnit.SECONDS);
        MoreExecutors.shutdownAndAwaitTermination(mAckExecutor,20,TimeUnit.SECONDS);
    }
}
