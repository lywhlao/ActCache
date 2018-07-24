/**
 * @(#)NullObject.JAVA, 2017年11月04日.
 * <p>
 * Copyright 2017 Netease, Inc. All rights reserved. NETEASE PROPRIETARY/CONFIDENTIAL. Use is
 * subject to license terms.
 */
package com.netease.act.cache.bean;

import java.io.Serializable;

/**
 * 用于序列化null对象
 *
 * @author hzwangliyuan.
 */
public class NullObject implements Serializable {

    private static final long serialVersionUID = 1194103533485860553L;

    private static final NullObject INSTANCE = new NullObject();

    private NullObject() {

    }

    public static NullObject getInstance() {
        return INSTANCE;
    }

    public static boolean isNullObject(Object compareObject) {
        return INSTANCE == compareObject;
    }

}
