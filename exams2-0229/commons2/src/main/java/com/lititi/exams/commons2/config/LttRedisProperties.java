package com.lititi.exams.commons2.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ltt.cache.redis")
@Data
@NoArgsConstructor
public class LttRedisProperties {

    private Other other;
    private Session session;
    private Pool pool;

    @Data
    @NoArgsConstructor
    public static class Other {
        private Master master;
        private Slave slave;

        @Data
        @NoArgsConstructor
        public static class Master {
            private String url;
            private String host = "localhost";
            private String username;
            private String password;
            private int port = 6379;
            private boolean ssl;
            private Duration timeout;
            private Duration connectTimeout;
        }

        @Data
        @NoArgsConstructor
        public static class Slave {
            private String url;
            private String host;
            private String username;
            private String password;
            private int port;
            private boolean ssl;
            private Duration timeout;
            private Duration connectTimeout;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Session {
        private Master master;
        private Slave slave;

        @Data
        @NoArgsConstructor
        public static class Master {
            private String url;
            private String host = "localhost";
            private String username;
            private String password;
            private int port = 6379;
            private boolean ssl;
            private Duration timeout;
            private Duration connectTimeout;
        }

        @Data
        @NoArgsConstructor
        public static class Slave {
            private String url;
            private String host;
            private String username;
            private String password;
            private int port;
            private boolean ssl;
            private Duration timeout;
            private Duration connectTimeout;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Pool {
        private int maxIdle = 8;
        private int minIdle = 0;
        private int maxActive = 8;
        private Duration maxWait = Duration.ofMillis(-1L);
        private Duration timeBetweenEvictionRuns;
    }
}
