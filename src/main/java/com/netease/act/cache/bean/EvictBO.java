package com.netease.act.cache.bean;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "uuid")
public class EvictBO {

    String uuid;

    String cacheName;

    String key;

    String ip;

    /**
     *  1 broadcast
     *  2 commit
     */
    int phase;
}
