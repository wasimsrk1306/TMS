package com.wasim.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync 
public class AsyncConfig {


	@Bean("AsyncExecution") 
	public TaskExecutor getAsyncExecutor() {
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		
		executor.setCorePoolSize(50);
		executor.setMaxPoolSize(1000);
		executor.setQueueCapacity(100);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("AsyncExecution-");
		executor.initialize();
		
		return executor; 
	}

}