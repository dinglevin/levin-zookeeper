package org.levin.zookeeper;

import java.util.concurrent.TimeUnit;

import org.levin.zookeeper.RetryCounterConfig.ExponentialBackoffPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryCounter {
    private static final Logger logger = LoggerFactory.getLogger(RetryCounter.class);

    private RetryCounterConfig retryConfig;
    private int attempts;

    public RetryCounter(int maxAttempts, long sleepInterval, TimeUnit timeUnit) {
        this(new RetryCounterConfig(maxAttempts, sleepInterval, -1, timeUnit,
                new ExponentialBackoffPolicy()));
    }

    public RetryCounter(RetryCounterConfig retryConfig) {
        this.attempts = 0;
        this.retryConfig = retryConfig;
    }

    public int getMaxAttempts() {
        return retryConfig.getMaxAttempts();
    }

    public void sleepUntilNextRetry() throws InterruptedException {
        int attempts = getAttemptTimes();
        long sleepTime = retryConfig.getBackoffTime(attempts);
        logger.info("Sleeping " + sleepTime + "ms before retry #" + attempts + "...");
        retryConfig.getTimeUnit().sleep(sleepTime);
        useRetry();
    }

    public boolean shouldRetry() {
        return attempts < retryConfig.getMaxAttempts();
    }

    public void useRetry() {
        attempts++;
    }

    public boolean isRetry() {
        return attempts > 0;
    }

    public int getAttemptTimes() {
        return attempts;
    }
}
