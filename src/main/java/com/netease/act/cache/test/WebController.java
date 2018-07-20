package com.netease.act.cache.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {


    @Autowired
    CacheService cacheService;


//    http://localhost:8500/put
    @RequestMapping("/put")
    public Object test(@RequestParam(defaultValue = "laojiaqi") String value){
        return cacheService.test(value);
    }

    //    http://localhost:8500/evict
    @RequestMapping("/evict")
    public Object evict(@RequestParam(defaultValue = "laojiaqi") String value){
        return cacheService.evict(value);
    }

}
