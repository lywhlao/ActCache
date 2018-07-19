package com.netease.act.cache.util;

import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * 日志帮助类
 * 
 * @author wangyusong
 */
public class LoggerUtil {
    /** 线程编号修饰符 */
    private static final char THREAD_RIGHT_TAG = ']';

    /** 线程编号修饰符 */
    private static final char THREAD_LEFT_TAG = '[';

    /**
     * warn日志打印
     * 
     * @param logger
     * @param message
     * @param params
     */
    public static void warn(Logger logger, String message, Object... params) {
        logger.warn(getLogString(message, params));
    }

    /**
     * info日志打印LoggerUtil.warn(LOGGER, "[op:put] failed to write cache with key
     * {0}, message={1}", memKey, e.getMessage());
     * 
     * @param logger
     * @param message
     * @param params
     */
    public static void info(Logger logger, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(getLogString(message, params));
        }
    }

    /**
     * debug日志打印
     * 
     * @param logger
     * @param message
     * @param params
     */
    public static void debug(Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(getLogString(message, params));
        }
    }

    /**
     * error日志打印
     *
     * @param logger
     * @param message
     * @param params
     */
    public static void error(Logger logger, String message, Object... params) {
        logger.error(getLogString(message, params));

    }

    /**
     * error日志打印
     * 
     * @param logger
     * @param e
     * @param message
     * @param params
     */
    public static void error(Logger logger, Exception e, String message,
        Object... params) {
        logger.error(getLogString(message, params), e);

    }

    /**
     * 生成输出到日志的字符串
     * 
     * @param message
     * @param params
     * @return
     */
    public static String getLogString(String message, Object... params) {
        try {
            StringBuilder log = new StringBuilder();
            log.append(THREAD_LEFT_TAG).append(Thread.currentThread().getId())
                .append(THREAD_RIGHT_TAG);
            log.append(formatMessage(message, params));
            return log.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化日志
     * 
     * @param message
     * @param params
     * @return
     */
    private static String formatMessage(String message, Object[] params) {
        return message != null && params != null && params.length != 0
            ? MessageFormat.format(message, params)
            : message;
    }

}
