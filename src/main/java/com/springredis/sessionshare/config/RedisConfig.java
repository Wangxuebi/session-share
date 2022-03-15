package com.springredis.sessionshare.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisConfig {

    /**
     * JedisPool相关配置
     */
    @Component
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public class JedisPoolConfigProp {
        Integer maxIdle;
        Integer minIdle;
        Integer maxActive;

        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Integer getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(Integer minIdle) {
            this.minIdle = minIdle;
        }

        public Integer getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(Integer maxActive) {
            this.maxActive = maxActive;
        }
    }

    /**
     * Cluster节点相关配置
     */
    @Component
    @ConfigurationProperties(prefix = "spring.redis.cluster")
    public class ClusterConfigProp {
        List<String> nodes;

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }
    }

    /**
     * Cluster相关配置
     */
    @Configuration
    public class RedisClusterConfig {

        @Autowired
        private ClusterConfigProp clusterConfigProp;

        @Autowired
        private JedisPoolConfigProp jedisPoolConfigProp;

        @Bean
        public JedisCluster jedisCluster() {
            Set<HostAndPort> nodeSet = new HashSet<>();
            for (String node : clusterConfigProp.getNodes()) {
                String[] split = node.split(":");
                nodeSet.add(new HostAndPort(split[0], Integer.valueOf(split[1])));
            }

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(jedisPoolConfigProp.getMaxActive());
            poolConfig.setMaxIdle(jedisPoolConfigProp.getMaxIdle());
            poolConfig.setMinIdle(jedisPoolConfigProp.getMinIdle());

            JedisCluster jedisCluster = new JedisCluster(nodeSet, poolConfig);
            return jedisCluster;
        }
    }
}
