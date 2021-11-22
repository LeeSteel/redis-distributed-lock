package com.lg.redislock.web;


import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("redislock")
public class RedislockController {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping("/write")
    public String write() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("wr-lock");
        RLock writeLock = readWriteLock.writeLock();
        String s = UUID.randomUUID().toString();
        writeLock.lock();
        try {
            redisTemplate.opsForValue().set("wr-lock-key", s);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
        return s;
    }

    @RequestMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("wr-lock");
        RLock readLock = readWriteLock.readLock();
        String s = "";
        readLock.lock();
        try {
            s = redisTemplate.opsForValue().get("wr-lock-key");
        } finally {
            readLock.unlock();
        }
        return s;
    }


    @RequestMapping("/redisson")
    public String testRedisson() {
        //获取分布式锁，只要锁的名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("lock");

        boolean isLock;
        try {
            //加锁（阻塞等待），默认过期时间是30秒
            // lock.lock();


            // 尝试获取分布式锁
            isLock = lock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
            if (isLock) {
                //TODO  获取锁成功，执行业务逻辑
                //如果业务执行过长，Redisson会自动给锁续期
                Thread.sleep(15000);
            }

            System.out.println("加锁成功，执行业务逻辑");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //解锁，如果业务执行完成，就不会继续续期，即使没有手动释放锁，在30秒过后，也会释放锁
            lock.unlock();
        }

        return "Hello Redisson!";
    }
}
