package com.netease.act.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ZKService {




    @PostConstruct
    public CuratorFramework getZKClient(){
        CuratorFramework zkc = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
                new ExponentialBackoffRetry(1000, 3));
        try {
            zkc.start();
            zkc.getZookeeperClient().blockUntilConnectedOrTimedOut();
        } catch (Exception e) {
            log.error("getZKClient connect error",e);
        }
        return zkc;
    }

}
