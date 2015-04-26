package org.levin.zookeeper;

import java.util.concurrent.TimeUnit;

public class RetryCounterConfig {
    private int maxAttempts;
    private long sleepInterval;
    private long maxSleepTime;
    private TimeUnit timeUnit;
    private BackoffPolicy backoffPolicy;

    private static final BackoffPolicy DEFAULT_BACKOFF_POLICY = new ExponentialBackoffPolicy();

    public RetryCounterConfig() {
        maxAttempts = 1;
        sleepInterval = 1000;
        maxSleepTime = -1;
        timeUnit = TimeUnit.MILLISECONDS;
        backoffPolicy = DEFAULT_BACKOFF_POLICY;
    }

    public RetryCounterConfig(int maxAttempts, long sleepInterval,
            long maxSleepTime, TimeUnit timeUnit, BackoffPolicy backoffPolicy) {
        this.maxAttempts = maxAttempts;
        this.sleepInterval = sleepInterval;
        this.maxSleepTime = maxSleepTime;
        this.timeUnit = timeUnit;
        this.backoffPolicy = backoffPolicy;
    }

    public RetryCounterConfig setBackoffPolicy(BackoffPolicy backoffPolicy) {
        this.backoffPolicy = backoffPolicy;
        return this;
    }

    public RetryCounterConfig setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }

    public RetryCounterConfig setMaxSleepTime(long maxSleepTime) {
        this.maxSleepTime = maxSleepTime;
        return this;
    }

    public RetryCounterConfig setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
        return this;
    }

    public RetryCounterConfig setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getMaxSleepTime() {
        return maxSleepTime;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public BackoffPolicy getBackoffPolicy() {
        return backoffPolicy;
    }
    
    public long getBackoffTime(int attempts) {
        return backoffPolicy.getBackoffTime(this, attempts);
    }

    public static class BackoffPolicy {
        public long getBackoffTime(RetryCounterConfig config, int attempts) {
            return config.getSleepInterval();
        }
    }

    public static class ExponentialBackoffPolicy extends BackoffPolicy {
        @Override
        public long getBackoffTime(RetryCounterConfig config, int attempts) {
            long backoffTime = (long) (config.getSleepInterval() * Math.pow(2, attempts));
            return backoffTime;
        }
    }

    public static class ExponentialBackoffPolicyWithLimit extends
            ExponentialBackoffPolicy {
        @Override
        public long getBackoffTime(RetryCounterConfig config, int attempts) {
            long backoffTime = super.getBackoffTime(config, attempts);
            return config.getMaxSleepTime() > 0 ? Math.min(backoffTime,
                    config.getMaxSleepTime()) : backoffTime;
        }
    }
}
