package com.mmwwtt.demo.redis.demo.redisson;

import com.mmwwtt.demo.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/reids/redisson")
@Slf4j
public class RedissonDemo {

    @PostMapping("/demoBase")
    public ApiResponse<Void> demoBase() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0);

        //设置编码，防止插入redis后乱码
        config.setCodec(StringCodec.INSTANCE);

        //获取客户端
        RedissonClient redissonClient = Redisson.create(config);

        //获取所有的key
        redissonClient.getKeys().getKeys().forEach(key -> log.info(key));

        //往redis插入列表
        RList<String> list = redissonClient.getList("numberList");
        list.add("123");
        list.remove("321");

        //往redis插入无序集合
        RSet<String> set = redissonClient.getSet("numberSet");
        set.add("123");
        set.remove("321");

        //往redis插入map
        RMap<String, Integer> map = redissonClient.getMap("numberMap");
        map.put("number1", 123);
        map.remove("number2");
        map.expire(10, TimeUnit.SECONDS);

        //往redis插入键值对
        RBucket key1 = redissonClient.getBucket("key1");
        key1.set("number1");
        map.expire(10, TimeUnit.SECONDS);

        //关闭客户端
        redissonClient.shutdown();
        return ApiResponse.success();
    }

    @PostMapping("/demoRDelayedQueue")
    public ApiResponse<Void> demoRDelayedQueue() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0);

        //设置编码，防止插入redis后乱码
        config.setCodec(StringCodec.INSTANCE);

        //获取客户端
        RedissonClient redissonClient = Redisson.create(config);

        //分布式延迟队列
        RBlockingDeque<String> blockingDeque = redissonClient.getBlockingDeque("orderQueue");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);

        log.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "添加任务到延时队列里面");
        delayedQueue.offer("添加一个任务", 3, TimeUnit.SECONDS);
        delayedQueue.offer("添加二个任务", 6, TimeUnit.SECONDS);
        delayedQueue.offer("添加三个任务", 9, TimeUnit.SECONDS);

        try {
            // 阻塞等待并执行
            while (true){
                String task = blockingDeque.take();
                log.info("任务执行中");
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        //关闭客户端
        redissonClient.shutdown();
        return ApiResponse.success();
    }

    //分布式锁 红锁demo
    @PostMapping("/testRedLock")
    public ApiResponse<Void>  testRedLock() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client1 = Redisson.create(config);

        RLock lock1 = client1.getLock("lock1");
        RLock lock2 = client1.getLock("lock2");
        RLock lock3 = client1.getLock("lock3");
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);

        try {
            // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean res = redLock.tryLock(100, 10, TimeUnit.SECONDS);
            if (res) {
                //成功获得锁，在这里处理业务
                log.info("成功获取到锁...");
            }
        } catch (Exception e) {
            throw new RuntimeException("aquire lock fail");
        } finally {
            // 无论如何, 最后都要解锁
            redLock.unlock();
        }
        return ApiResponse.success();
    }
}
