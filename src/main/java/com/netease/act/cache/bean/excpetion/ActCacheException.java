package com.netease.act.cache.bean.excpetion;

public class ActCacheException extends RuntimeException{
    public ActCacheException() {
    }

    public ActCacheException(String message) {
        super(message);
    }

    public ActCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActCacheException(Throwable cause) {
        super(cause);
    }

    public ActCacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
