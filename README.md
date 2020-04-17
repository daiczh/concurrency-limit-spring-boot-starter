# concurrency-limit-spring-boot-starter
通过自定义注解+AOP针对服务的方法进行并发数量的控制；

此功能仅限单个服务的方法限制，如果是需要对集群总体的限制，需要结合机器数量修改对应并发数量配置。

# 具体用法
**开启：**

```java
/**
 * 程序入口
 * 
 * @author dcz
 *
 */
@SpringBootApplication
@EnableConcurrencyLimit
public class MyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}
}
```



**使用（任意方法）：**

```java
/**
* 方法XXX
*/
@ConcurrencyLimit(useGlobalConfig = true)
public void testMethod() {

	// TODO
}
```



**注解参数说明：**

```java
/**
 * 是否使用全局配置项，默认不使用<br>
 * 
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
```



**配置项（前提是useGlobalConfig = true）：**

```yaml
concurrency-limit:

    #最大并发数量
    maxConcurrentNum: 100
    
    #最大等待时长，单位：秒
    maxWaitTime: 3
```

