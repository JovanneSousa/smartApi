package com.jovanne.smartApi.infraestructure.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfig {
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory factory
    ) {
        var template = new RedisTemplate<String , String>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
