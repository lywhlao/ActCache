package com.netease.act.cache.service;

import com.netease.act.cache.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class ZKService implements Constant {



    @PostConstruct
    public CuratorFramework getZKClient(){
        CuratorFramework zkc = CuratorFrameworkFactory.newClient(ZK_ADDRESS,
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
