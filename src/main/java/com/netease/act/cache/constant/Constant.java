package com.netease.act.cache.constant;

public interface Constant {


    /**
     *
     */
     int PHASE_BROADCAST = 1;

    /**
     *
     */
     int PHASE_COMMIT = 2;


     String ACT_CACHE_EVICT_QUEUE = "act_cache_evict_queue";

     String ACT_CACHE_ACK_QUEUE = "act_cache_ack_queue";

     String ZK_ADDRESS = "127.0.0.1:2181";

     int LOCAL_CACHE_MAXIMUM_SIZE = 5210;

     int LOCAL_CACHE_EXPIRE_DURATION = 10;
}
