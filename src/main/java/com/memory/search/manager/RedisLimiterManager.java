package com.memory.search.manager;

import com.memory.search.common.ErrorCode;
import com.memory.search.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 邓哈哈
 * 2023/10/10 22:23
 * Function: 用户调用接口限流实现
 * Version 1.0
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流实现
     *
     * @param key 识别用户的key
     */
    public void doRateLimit(String key) {
        // 创建限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了之后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        // 超出发放令牌数目，请求过于频繁
        ThrowUtils.throwIf(!canOp, ErrorCode.TOO_MANY_REQUEST);
    }
}
