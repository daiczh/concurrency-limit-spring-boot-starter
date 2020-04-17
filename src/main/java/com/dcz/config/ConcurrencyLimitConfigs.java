package com.dcz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 限制并发配置项
 */
@Data
@ConfigurationProperties(prefix = "concurrency-limit")
public class ConcurrencyLimitConfigs {
	
	/**
	 * 最大并发数量
	 */
	private Long maxConcurrentNum = 1000L;

	/**
	 * 最大等待时长，单位：秒
	 */
	private Long maxWaitTime = 10L;
	
}
