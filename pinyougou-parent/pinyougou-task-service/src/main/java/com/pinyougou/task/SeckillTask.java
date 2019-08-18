package com.pinyougou.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {

    @Scheduled(cron = "* * * * * ?")
    public void testTask() throws Exception {
        System.out.println(new Date());
    }
}