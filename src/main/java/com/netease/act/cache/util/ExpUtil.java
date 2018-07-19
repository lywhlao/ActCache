package com.netease.act.cache.util;

import com.netease.act.cache.bean.excpetion.ActCacheException;

public class ExpUtil {


    public static void check(boolean exp){
        if(!exp){
            throw new ActCacheException();
        }
    }

}
