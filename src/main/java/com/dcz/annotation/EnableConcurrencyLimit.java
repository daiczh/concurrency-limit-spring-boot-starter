package com.dcz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.dcz.interceptor.ConcurrencyLimitAopInterceptor;

/**
 * 启动并发限制
 * 
 * @author dcz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ConcurrencyLimitAopInterceptor.class})
public @interface EnableConcurrencyLimit {
}
