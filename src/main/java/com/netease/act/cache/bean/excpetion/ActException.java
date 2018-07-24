package com.netease.act.cache.bean.excpetion;

public class ActException extends RuntimeException{
    public ActException() {
    }

    public ActException(String message) {
        super(message);
    }

    public ActException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActException(Throwable cause) {
        super(cause);
    }

    public ActException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}