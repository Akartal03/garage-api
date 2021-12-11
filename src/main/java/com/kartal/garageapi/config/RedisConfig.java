package com.kartal.garageapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.mode}")
	private String redisMode;

	@Bean
	@Primary
	public RedisProperties redisProperties() {
		return new RedisProperties();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);
		final RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setDefaultSerializer(new StringRedisSerializer());
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.setEnableTransactionSupport(false);
		return template;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisProperties properties = redisProperties();
		if (redisMode.equals("cluster")) {
			RedisClusterConfiguration configuration = new RedisClusterConfiguration(properties.getCluster().getNodes());
			configuration.setMaxRedirects(properties.getCluster().getMaxRedirects());
			if (!properties.getPassword().isEmpty()) {
				configuration.setPassword(properties.getPassword());
			}
			return new JedisConnectionFactory(configuration);
		} else {

			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
			configuration.setHostName(properties.getHost());
			configuration.setPort(properties.getPort());
			if (!properties.getPassword().isEmpty()) {
				configuration.setPassword(properties.getPassword());
			}
			return new JedisConnectionFactory(configuration);
		}

	}
}
