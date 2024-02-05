package com.memory.search.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author 邓哈哈
 * 2024/2/4 17:55
 * Function:
 * Version 1.0
 */

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void test1() {
        // redisTemplate.opsForZSet().add("zset1", "text", 10);
        // redisTemplate.opsForZSet().add("zset1", "text2", 11);
        // redisTemplate.opsForZSet().add("zset1", "text3", 12);
        Double newScore = redisTemplate.opsForZSet().incrementScore("zset1", "text2",1);
    }


    @Test
    void test2() {
        Double score = redisTemplate.opsForZSet().score("zset1", "text");
        System.out.println(score);
        Long size = redisTemplate.opsForZSet().zCard("zset");
        System.out.println(size);
    }


    @Test
    void test3() {
        ThreadLocal<Long> longThreadLocal = new ThreadLocal<>();
        longThreadLocal.set(2L);

        Long aLong = longThreadLocal.get();
        System.out.println(aLong);
    }

}
