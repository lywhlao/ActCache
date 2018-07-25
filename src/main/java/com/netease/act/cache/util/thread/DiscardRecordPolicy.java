package com.netease.act.cache.util.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class DiscardRecordPolicy extends ThreadPoolExecutor.DiscardPolicy{

    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        log.error("threadPool DiscardRecordPolicy reject thread:{}",Thread.currentThread().getName());
    }
}
