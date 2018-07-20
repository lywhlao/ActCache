package com.netease.act.cache.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ZKService {




    @PostConstruct
    public CuratorFramework getZKClient(){
        CuratorFramework zkc = CuratorFrameworkFactory.newClient("10.242.1.219:2181",
                new ExponentialBackoffRetry(1000, 3));
        try {
            zkc.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zkc;
    }

}
