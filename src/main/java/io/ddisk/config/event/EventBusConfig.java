package io.ddisk.config.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import io.ddisk.eventbus.SystemDataBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Configuration
public class EventBusConfig {
	/**
	 * 异步事件总线
	 *
	 * @return
	 */
	@Bean(name = "asyncEventBus")
	public EventBus asyncEventBusService() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int count = Runtime.getRuntime().availableProcessors();
		executor.setCorePoolSize(count);
		executor.setMaxPoolSize(count);
		executor.setKeepAliveSeconds(0);
		executor.setQueueCapacity(512);
		//线程前缀名称
		executor.setThreadNamePrefix("eventbus-thread-");
		executor.initialize();
		return new AsyncEventBus(executor);
	}

	/**
	 * 同步事件总线
	 *
	 * @return
	 */
	@Bean(name = "syncEventBus")
	public EventBus syncEventBusService() {

		return new EventBus(SystemDataBus.class.getSimpleName());
	}
}
