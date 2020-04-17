package com.dcz.interceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.dcz.annotation.ConcurrencyLimit;
import com.dcz.config.ConcurrencyLimitConfigs;
import com.dcz.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

/**
 * 并发限制的aop拦截器
 * 
 * @author dcz
 *
 */
@Aspect
@Configuration
@Slf4j
@EnableConfigurationProperties(ConcurrencyLimitConfigs.class)
public class ConcurrencyLimitAopInterceptor {

	/**
	 * 全局变量用来记录并发限制
	 */
	private ConcurrentHashMap<String, AtomicLong> globalRecord = new ConcurrentHashMap<>();

	@Autowired
	private ConcurrencyLimitConfigs configs;

	/**
	 * 方法执行
	 *
	 * @param joinPoint
	 *            方法切入口
	 * @param concurrencyLimit
	 *            并发限制的自定义注解
	 * @throws Throwable
	 */
	@Around("@annotation(concurrencyLimit)")
	public Object methodAround(final ProceedingJoinPoint joinPoint, ConcurrencyLimit concurrencyLimit) throws Throwable {

		// 生成方法标识
		String methodKey = getMethodKey(joinPoint);

		// 校验并发&执行原子加一
		checkConcurrent(concurrencyLimit, methodKey);

		try {
			// 执行方法
			return joinPoint.proceed();
		} finally {
			// 利用原子操作对并发数量还原：减一
			globalRecord.get(methodKey).getAndDecrement();
		}
	}

	/**
	 * 判断是否超过最大并发、且超过等待时间，内部通过线程暂停1秒*自定义注解配置的最大秒数来实现等待的。
	 * 
	 * @param concurrencyLimit
	 *            自定义注解相关信息
	 * @param methodKey
	 *            方法唯一标识
	 */
	private void checkConcurrent(ConcurrencyLimit concurrencyLimit, String methodKey) {

		/*
		 * 获取全局配置
		 */
		long max = concurrencyLimit.useGlobalConfig() ? configs.getMaxConcurrentNum() : concurrencyLimit.max();
		long timeout = concurrencyLimit.useGlobalConfig() ? configs.getMaxWaitTime() : concurrencyLimit.timeout();
		int waitCount = 0;

		// 记录当前方法信息及并发初始量：0
		globalRecord.putIfAbsent(methodKey, new AtomicLong(0L));

		log.debug("方法:{},目前并发量:{}", methodKey, globalRecord.get(methodKey).get());

		// 执行判断
		while (globalRecord.get(methodKey).get() > max) {
			if (++waitCount > timeout) {
				log.error("方法:{},目前并发量:{},已达到最大值，且等待时间已超过:{}秒，抛出异常", methodKey, globalRecord.get(methodKey).get(), timeout);
				throw new ServiceException("too frequent operation!");
			}
			try {
				Thread.sleep(1 * 1000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			log.debug("方法:{},等待第:{}秒,目前并发:{}/{}", methodKey, waitCount, globalRecord.get(methodKey).get(), max);
		}

		// 通过后利用原子操作进行记录并发数量加一
		globalRecord.get(methodKey).getAndIncrement();

	}

	/**
	 * 生成方法唯一标识：类路径+方法名称
	 * 
	 * @param joinPoint
	 *            方法切入口
	 * @return 方法唯一标识
	 */
	private String getMethodKey(JoinPoint joinPoint) {
		return new StringBuilder(joinPoint.getTarget().getClass().getName()).append("#").append(joinPoint.getSignature().getName()).toString();
	}

}
