package com.pinyougou.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {

    /**
     * 定时任务测试
     * @throws Exception
     */
    @Scheduled(cron = "* * * * * ?")
    public void testTask() throws Exception {
        System.out.println(new Date());
    }
}