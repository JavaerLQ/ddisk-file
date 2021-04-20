package io.ddisk.eventbus;

import com.google.common.eventbus.EventBus;
import io.ddisk.eventbus.event.CrazyEvent;
import io.ddisk.eventbus.listener.SyncLockListener;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Slf4j
@Component
public class SystemDataBus implements InitializingBean {

	/**
	 * 计划线程池
	 */
	@Autowired
	@Qualifier("delayExecutor")
	private ThreadPoolTaskScheduler delayThreadPoolExecutor;

	/**
	 * 监听者
	 */
	@Autowired
	private SyncLockListener syncLockListener;

	/**
	 * 同步事件总线
	 */
	private final EventBus syncEventBus;

	/**
	 * 异步事件总线
	 */
	private final EventBus asyncEventBus;

	public SystemDataBus(@Qualifier("asyncEventBus") EventBus asyncEventBus, @Qualifier("syncEventBus") EventBus syncEventBus) {
		this.asyncEventBus = asyncEventBus;
		this.syncEventBus = syncEventBus;
	}

	/**
	 * 同步
	 *
	 * @param event
	 */
	public void postSync(CrazyEvent event) {
		syncEventBus.post(event);
	}

	/**
	 * 异步
	 *
	 * @param event
	 */
	public void postAsync(CrazyEvent event) {
		log.info("Event raise with type " + event.getClass().getSimpleName() + " and delay " + event.getDelay());
		if (event.getDelay() <= 0L) {
			asyncEventBus.post(event);
		} else {
			delayThreadPoolExecutor.schedule(() ->
							Try.run(() -> asyncEventBus.post(event)).onFailure(e -> log.debug("Event Bus "+e.getMessage())), event.getDueDate());
		}
	}

	/**
	 * 注册Listener
	 */
	@Override
	public void afterPropertiesSet(){
		syncEventBus.register(syncLockListener);
		asyncEventBus.register(syncLockListener);
	}
}
