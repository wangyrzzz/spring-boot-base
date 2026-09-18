package com.example.demo.aop;

import com.example.demo.annotation.Resubmit;
import com.example.demo.common.ApiException;
import com.example.demo.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 防重复提交aop
 * @Author: WangYuanrong
 * @Date: 2021/6/24 9:40
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(RedissonClient.class)
public class ResubmitAspect {

    @Autowired
    private RedissonClient redissonClient;

    public boolean fairLock(String lockKey, int leaseTime, TimeUnit unit) {
        RLock fairLock = redissonClient.getFairLock(lockKey);
        try {
            return fairLock.tryLock(3, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("try lock error:", e);
        }
        return false;
    }

    /**
     * 防重复提交aop
     * @param joinPoint
     * @param resubmit
     * @return
     * @throws Throwable
     */
    @Around("@annotation(resubmit)")
    public Object resubmit(ProceedingJoinPoint joinPoint, Resubmit resubmit) throws Throwable {
        if (Objects.nonNull(resubmit)) {
            // 获取参数
            Object[] args = joinPoint.getArgs();
            // 进行一些参数的处理，比如获取订单号，操作人id等
            String prefix = Constant.RESUBMIT_LOCK_KEY;
            final Class<?> aClass = joinPoint.getTarget().getClass();
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String suffix = aClass.getName() + signature.getName() + Arrays.toString(joinPoint.getArgs());
            String key = prefix + md5(suffix);
            // 公平加锁，lockTime后锁自动释放
            boolean isLocked = false;
            try {
                isLocked = this.fairLock(key, resubmit.lockTime(), TimeUnit.MILLISECONDS);
                // 如果成功获取到锁就继续执行
                if (isLocked) {
                    return joinPoint.proceed();
                } else {
                    // 未获取到锁
                    throw new ApiException("请求处理中，请稍后");
                }
            } finally {
                if (isLocked) {
                    final RLock lock = redissonClient.getLock(key);
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        }
        return joinPoint.proceed();
    }

    private String md5(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(32);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JDK 不支持 MD5", e);
        }
    }
}
