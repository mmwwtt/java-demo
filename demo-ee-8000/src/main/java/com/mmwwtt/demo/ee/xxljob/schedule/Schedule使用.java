package com.mmwwtt.demo.ee.xxljob.schedule;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@Slf4j
public class Schedule使用{
    @Scheduled(cron = "* 10 * * * *")
    public void test_1() {
        log.info("定时任务测试");
    }
}
