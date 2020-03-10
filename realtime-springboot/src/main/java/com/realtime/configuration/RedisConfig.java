package com.realtime.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.hostName}")
    private String hostName;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${spring.redis.maxIdle}")
    private Integer maxIdle;
    @Value("${spring.redis.maxTotal}")
    private Integer maxTotal;
    @Value("${spring.redis.maxWaitMillis}")
    private Long maxWaitMillis;
    @Value("${spring.redis.testWhileIdle}")
    private Boolean testWhileIdle;
    @Value("${spring.redis.minEvictableIdleTimeMillis}")
    private Long minEvictableIdleTimeMillis;
    @Value("${spring.redis.numTestsPerEvictionRun}")
    private Integer numTestsPerEvictionRun;
    @Value("${spring.redis.timeBetweenEvictionRunsMillis}")
    private Long timeBetweenEvictionRunsMillis;

    @Bean
    public JedisClientConfiguration jedisClientConfiguration() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //取出值之前要检查
        jedisPoolConfig.setTestOnBorrow(true);
        //最大空闲线程数
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最大连接数
        jedisPoolConfig.setMaxTotal(maxTotal);
        //连接redis最大等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //连接池空闲时，进行redis有效性检查
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        //检查redis连接的最小空闲时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //运行检查的线程数，如果不启用检查，为-1
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        //运行检查线程启动的时间间隔，默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();

        JedisClientConfiguration jcf = builder.poolConfig(jedisPoolConfig).build();
        return jcf;
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory(JedisClientConfiguration jedisClientConfiguration) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        RedisClusterConfiguration config2 = new RedisClusterConfiguration();

        RedisNode node26379 = new RedisNode("192.168.56.111",6379);
        RedisNode node27000 = new RedisNode("192.168.56.111",7000);
        RedisNode node36379 = new RedisNode("192.168.56.112",6379);
        RedisNode node37000 = new RedisNode("192.168.56.112",7000);
        RedisNode node46379 = new RedisNode("192.168.56.113",6379);
        RedisNode node47000 = new RedisNode("192.168.56.113",7000);

        Set<RedisNode> rnodes = new HashSet<>();
        rnodes.add(node26379);
        rnodes.add(node27000);
        rnodes.add(node36379);
        rnodes.add(node37000);
        rnodes.add(node46379);
        rnodes.add(node47000);

        config2.setClusterNodes(rnodes);
        config.setHostName(hostName);
        config.setPort(port);
        config.setPassword(password);
        return new JedisConnectionFactory(config2,jedisClientConfiguration);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置v的序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        //设置key的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;

    }
}
