package com.example.demo.config.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁实现基于Redisson
 */
@Slf4j
@Component
@ConditionalOnBean(RedissonClient.class)
public class RedissonLockClient {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取锁
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 加锁操作
     *
     * @return boolean
     */
    public boolean tryLock(String lockName, long expireSeconds) {
        return tryLock(lockName, 0, expireSeconds);
    }


    /**
     * 加锁操作
     *
     * @return boolean
     */
    public boolean tryLock(String lockName, long waitTime, long expireSeconds) {
        RLock rLock = getLock(lockName);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(waitTime, expireSeconds, TimeUnit.SECONDS);
            if (getLock) {
                log.info("获取锁成功,lockName={}", lockName);
            } else {
                log.info("获取锁失败,lockName={}", lockName);
            }
        } catch (InterruptedException e) {
            log.error("获取式锁异常，lockName=" + lockName, e);
            getLock = false;
        }
        return getLock;
    }

    /**
     * 加锁操作
     *
     * @return boolean
     */
    public boolean tryLock(String lockName, long waitTime, long expireSeconds, TimeUnit unit) {
        RLock rLock = getLock(lockName);
        boolean getLock = false;
        try {
            getLock = rLock.tryLock(waitTime, expireSeconds, unit);
            if (getLock) {
                log.info("获取锁成功,lockName={}", lockName);
            } else {
                log.info("获取锁失败,lockName={}", lockName);
            }
        } catch (InterruptedException e) {
            log.error("获取式锁异常，lockName=" + lockName, e);
            getLock = false;
        }
        return getLock;
    }

    public boolean fairLock(String lockKey, int leaseTime, TimeUnit unit) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            return fairLock.tryLock(3, leaseTime, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 锁lockKey
     *
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 锁lockKey
     *
     * @param lockKey
     * @param leaseTime 秒
     * @return
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 锁lockKey
     *
     * @param lockKey
     * @param leaseTime
     * @return
     */
    public RLock lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = getLock(lockKey);
        lock.lock(leaseTime, unit);
        return lock;
    }

    /**
     * 解锁
     *
     * @param lockName 锁名称
     */
    public void unlock(String lockName) {
        try {
            final RLock lock = redissonClient.getLock(lockName);
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("解锁异常，lockName=" + lockName, e);
        }
    }


}
