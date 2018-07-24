package com.netease.act.cache.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * 线程池工具类
 * Created by hzlaojiaqi on 2017/9/12.
 */
public class ThreadPoolUtil {

    private ThreadPoolUtil() {

    }

    public static void execute(Runnable runnable){
        getInstance().execute(runnable);
    }
    public static Future submit(Runnable runnable){
        return getInstance().submit(runnable);
    }

    private static class InstanceHolder{
        private static ExecutorService executorService= Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * 3,new CustomThreadFactory());
    }

    public static ExecutorService getInstance(){
        return InstanceHolder.executorService;
    }
}
