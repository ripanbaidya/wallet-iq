package com.walletiq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.walletiq.constant.CacheNames;
import com.walletiq.dto.categories.CategoryResponse;
import com.walletiq.dto.dashboard.DashboardResponse;
import com.walletiq.dto.paymentmode.PaymentModeResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory,
                                     @Qualifier("redisObjectMapper") ObjectMapper mapper) {

        // -- Serializer per cache -- each knows its exact return type
        var dashboardSerializer = new Jackson2JsonRedisSerializer<>(mapper, DashboardResponse.class);

        CollectionType categoryListType = mapper.getTypeFactory()
            .constructCollectionType(List.class, CategoryResponse.class);
        var categorySerializer = new Jackson2JsonRedisSerializer<>(mapper, categoryListType);

        CollectionType paymentModeListType = mapper.getTypeFactory()
            .constructCollectionType(List.class, PaymentModeResponse.class);
        var paymentModeSerializer = new Jackson2JsonRedisSerializer<>(mapper, paymentModeListType);

        // -- Base config --
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .disableCachingNullValues();

        // -- Per-cache config with correct serializer + TTL --
        Map<String, RedisCacheConfiguration> configs = Map.of(
            CacheNames.DASHBOARD, base
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(dashboardSerializer)),

            CacheNames.CATEGORIES, base
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(categorySerializer)),

            CacheNames.PAYMENT_MODES, base
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                    .fromSerializer(paymentModeSerializer))
        );

        return RedisCacheManager.builder(factory)
            .cacheDefaults(base)
            .withInitialCacheConfigurations(configs)
            .build();
    }
}