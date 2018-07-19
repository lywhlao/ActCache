package com.netease.act.cache.core;

import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by laojiaqi on 2017/11/28.
 */
public abstract class RefreshAsyncCacheLoader<K, V> extends CacheLoader<K, V> {

    private ExecutorService mExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    private static final Logger Logger = LoggerFactory.getLogger(RefreshAsyncCacheLoader.class);

    @Override
    public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception {
        ListenableFutureTask<V> task = ListenableFutureTask.create(new Callable<V>() {
            public V call() {
                try {
                    return load((K) key);
                } catch (Exception e) {
                    Logger.error("RefreshAsyncCacheLoader exception", e);
                }
                return oldValue;
            }
        });
        mExecutors.execute(task);
        return task;
    }

    @PreDestroy
    public void destroy() {
        MoreExecutors.shutdownAndAwaitTermination(mExecutors, 2, TimeUnit.MINUTES);
    }

}
