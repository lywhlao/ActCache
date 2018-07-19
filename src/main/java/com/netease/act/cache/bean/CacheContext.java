package com.netease.act.cache.bean;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheContext {

     CacheBuilder<Object, Object> cacheBuilder;

     CacheLoader<Object, Object> cacheLoader;


}
