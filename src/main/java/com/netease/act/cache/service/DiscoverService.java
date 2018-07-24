package com.netease.act.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DiscoverService implements InitializingBean {

    public static final String WATCH_DIR = "/act_cache";

    public static final String ACT_CACHE_WORKER = "/act_cache/worker-";

    CuratorFramework zkc;

    PathChildrenCache pcc;

    @Autowired
    ZKService mZKService;

    @Autowired
    LeaderService mLeader;

    @Override
    public void afterPropertiesSet() throws Exception {

        zkc=mZKService.getZKClient();

        initTaskDir(zkc);

        register();

        pcc = new PathChildrenCache(zkc, WATCH_DIR, false);
        pcc.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        pcc.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                log.info("path child cache event==>{}",event);
                switch (event.getType()){
                    case CHILD_REMOVED:
                        mLeader.recheckCountMap();
                        break;
                }
            }
        });

    }


    /**
     * client register with zk
     */
    public void register() {
        try {
            zkc.create()
               .withProtection()
               .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
               .forPath(ACT_CACHE_WORKER);
        } catch (Exception e) {
            log.error("zk register error ", e);
        }
    }


    public List<ChildData> getChildren() {
        return pcc.getCurrentData();
    }

    public int getClientSize(){
        List<ChildData> currentData = pcc.getCurrentData();
        if(CollectionUtils.isEmpty(currentData)){
           return 0;
       }
       return currentData.size();
    }


    /**
     * init client root dir
     * @param zkc
     */
    private void initTaskDir(CuratorFramework zkc) {
        try {
            zkc.checkExists().creatingParentContainersIfNeeded().forPath(WATCH_DIR);
        } catch (Exception e) {
            log.error("initTaskDir error", e);
        }
    }


}
