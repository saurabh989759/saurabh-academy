package com.academy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * Configuration for distributed locking using Spring Integration's RedisLockRegistry
 * This is the Spring-native solution for distributed locks
 */
@Configuration
public class DistributedLockConfig {
    
    /**
     * RedisLockRegistry provides distributed locking using Redis
     * It implements the standard Java Lock interface
     * 
     * @param redisConnectionFactory Redis connection factory
     * @return RedisLockRegistry instance
     */
    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        // Registry key prefix and expiration time (40 seconds default)
        // Locks automatically expire to prevent deadlocks
        return new RedisLockRegistry(redisConnectionFactory, "academy-locks", 40000);
    }
}

