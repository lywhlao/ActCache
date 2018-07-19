package com.netease.act.cache.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class DiscoverService {



    public void register(){
        CuratorFramework zkc= CuratorFrameworkFactory.newClient("10.242.1.219:2181",
                new ExponentialBackoffRetry(1000,3));
        zkc.start();
        zkc.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
           .forPath(MessageMiddle.TASK_DIR);
    }

}
