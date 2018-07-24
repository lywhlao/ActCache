package com.netease.act.cache.util.thread;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by laojiaqi on 2017/9/12.
 */
@Slf4j
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("uncaughtException threadName:{} detail:{}",t.getName(),e);
    }
}
