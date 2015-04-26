package org.levin.zookeeper;

import java.util.concurrent.TimeUnit;

import org.levin.zookeeper.RetryCounterConfig.ExponentialBackoffPolicy;

public class RetryCounterFactory {
    private final RetryCounterConfig retryConfig;

    public RetryCounterFactory(int maxAttempts, int sleepIntervalMillis) {
        this(new RetryCounterConfig(maxAttempts, sleepIntervalMillis, -1,
                TimeUnit.MILLISECONDS, new ExponentialBackoffPolicy()));
    }

    public RetryCounterFactory(RetryCounterConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    public RetryCounter create() {
        return new RetryCounter(retryConfig);
    }
}
