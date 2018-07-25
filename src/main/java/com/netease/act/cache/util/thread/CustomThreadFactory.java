package com.netease.act.cache.util.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by laojiaqi on 2017/9/12.
 */
public class CustomThreadFactory implements ThreadFactory {

    private static final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

    private AtomicInteger threadNum=new AtomicInteger(0);

    private static final String THREAD_NAME_PREFIX="act_cache_thread_member_";

    private final Thread.UncaughtExceptionHandler handler=new CustomExceptionHandler();

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = defaultFactory.newThread(r);
        thread.setName(THREAD_NAME_PREFIX+threadNum.incrementAndGet());
        thread.setUncaughtExceptionHandler(handler);
        thread.setDaemon(true);
        return thread;
    }

    private CustomThreadFactory(){

    }

    public static CustomThreadFactory getInstance(){
        return CustomThreadFactoryHolder.getInstance();
    }

    public static class CustomThreadFactoryHolder{
        private static CustomThreadFactory instance=new CustomThreadFactory();
        public static CustomThreadFactory getInstance(){
            return instance;
        }
    }
}
