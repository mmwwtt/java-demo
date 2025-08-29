package com.mmwwtt.demo.ee.stopWatch;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

public class StopWatchTest {
    @Test
    public void test() throws InterruptedException {
        StopWatch sw = new StopWatch("任务总览");

        sw.start("任务A");
        Thread.sleep(120);
        sw.stop();

        sw.start("任务B");
        Thread.sleep(230);
        sw.stop();

        sw.start("任务C");
        Thread.sleep(350);
        sw.stop();

        // 打印美观的汇总表
        System.out.println(sw.prettyPrint());
    }
}
