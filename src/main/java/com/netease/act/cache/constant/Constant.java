package com.netease.act.cache.constant;

public interface Constant {


    /**
     * first broadcast
     */
     int PHASE_BROADCAST = 1;

    /**
    *  second commit
     *
     */
     int PHASE_COMMIT = 2;


    /**
     * leader evict queue
     */
     String ACT_CACHE_EVICT_QUEUE = "act_cache_evict_queue";

    /**
     * leader ack queue
     */
     String ACT_CACHE_ACK_QUEUE = "act_cache_ack_queue";

    /**
     * zookeepr address
     */
//     String ZK_ADDRESS = "127.0.0.1:2181";

     String ZK_ADDRESS = "10.242.1.219:2181";

    /**
     *  local cache size
     */
     int LOCAL_CACHE_MAXIMUM_SIZE = 5210;

    /**
     *  local cache expire time  10s
     */
     int LOCAL_CACHE_EXPIRE_DURATION = 10;


    /**
     * leader select dir
     */
     String ACT_CACHE_LEADER = "/act_cache_leader";

}
