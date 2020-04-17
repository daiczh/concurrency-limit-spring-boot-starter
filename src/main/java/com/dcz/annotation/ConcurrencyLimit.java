package com.dcz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限制并发数量：利用全局变量记录当前方法的并发数进行限制
 * 
 * @author dcz
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConcurrencyLimit {

	/**
	 * 是否使用全局配置项，默认不使用，全局配置如下：<br>
	 * 
	 * <pre>
	 * concurrency-limit:
	 *     
	 *     #最大并发数量
	 *     maxConcurrentNum: 999
	 *     
	 *     #最大等待时长，单位：秒
	 *     maxWaitTime: 20
	 *     
	 * </pre>
	 * 
	 * @return
	 */
	boolean useGlobalConfig() default false;

	/**
	 * 最高并发数量，默认1000
	 * 
	 * @return
	 */
	long max() default 1000;

	/**
	 * 等待时长，超过此时长后抛出异常，单位：秒，默认10秒
	 * 
	 * @return
	 */
	long timeout() default 10;

}
