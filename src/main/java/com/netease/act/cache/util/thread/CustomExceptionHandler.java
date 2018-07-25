package com.netease.act.cache.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by laojiaqi on 2017/9/12.
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOG.error("uncaughtException,thread:{},error:{}", t.getName(),e);
    }
}
