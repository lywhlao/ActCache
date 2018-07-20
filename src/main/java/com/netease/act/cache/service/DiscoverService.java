package com.netease.act.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void afterPropertiesSet() throws Exception {

        zkc=mZKService.getZKClient();

        initTaskDir(zkc);

        pcc = new PathChildrenCache(zkc, WATCH_DIR, false);
        pcc.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

    }


    public void register() {
        try {
            zkc.create()
               .withProtection()
               .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
               .forPath(ACT_CACHE_WORKER);
        } catch (Exception e) {
            log.error("register error ", e);
        }
    }


    public List<ChildData> getChildren() {
        return pcc.getCurrentData();
    }


    public void initTaskDir(CuratorFramework zkc) {
        try {
            zkc.checkExists().creatingParentContainersIfNeeded().forPath(WATCH_DIR);
        } catch (Exception e) {
            log.error("initTaskDir error", e);
        }
    }


}
