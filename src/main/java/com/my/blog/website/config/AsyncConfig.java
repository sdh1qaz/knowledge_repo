package com.my.blog.website.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 使用Java配置定义线程池和启用异步
 * @author 苏登辉
 */

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer{
	
	
	//核心线程数
	@Value("${knowledge-repo.async.corePoolSize}")
    private int corePoolSize;
	
	//线程池最大线程数
	@Value("${knowledge-repo.async.maxPoolSize}")
	private int maxPoolSize;
	
	//线程队列最大线程数
	@Value("${knowledge-repo.async.queueCapacity}")
	private int queueCapacity;
	
	public Executor getAsyncExecutor() {
		//定义线程池
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		//核心线程数
		executor.setCorePoolSize(corePoolSize);
		//线程池最大线程数
		executor.setMaxPoolSize(maxPoolSize);
		//线程队列最大线程数
		executor.setQueueCapacity(queueCapacity);
		//初始化
		executor.initialize();
		return executor;
	}
}
