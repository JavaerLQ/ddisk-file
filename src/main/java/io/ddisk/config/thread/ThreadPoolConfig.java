package io.ddisk.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Configuration
public class ThreadPoolConfig {

	/**
	 * 延时执行线程池
	 *
	 * @return
	 */
	@Bean(name = "delayExecutor")
	public ThreadPoolTaskScheduler scheduleThreadPoolExecutor() {

		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolTaskScheduler.setThreadNamePrefix("delay-thread-");
		threadPoolTaskScheduler.initialize();
		return threadPoolTaskScheduler;
	}
}
