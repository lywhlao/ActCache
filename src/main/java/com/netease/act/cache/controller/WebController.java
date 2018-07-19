package com.netease.act.cache.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {



    @RequestMapping("/")
    public Object test(){
        return "hello";
    }

}
