package com.memory.search.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 邓哈哈
 * 2023/10/16 20:36
 * Function: Redisson 配置
 * Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissionConfig {
    private String host;

    private String port;

    private String password;

    private Integer database;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        //  使用单个Redis，没有开集群 useClusterServers()  设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(database).setPassword(password);
        // 2. 创建实例
        return Redisson.create(config);
    }
}
