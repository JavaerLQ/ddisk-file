package io.ddisk.eventbus.listener;

import com.google.common.eventbus.Subscribe;
import io.ddisk.eventbus.event.SyncLockEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static io.ddisk.eventbus.event.SyncLockEvent.LOCK_MAP;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Slf4j
@Data
@Service
public class SyncLockListener {

	/**
	 * 锁对象map的添加和移除
	 * @param event
	 */
	@Subscribe
	public void execute(SyncLockEvent event){
		if (!event.getUnlock()){
			// 加锁
			if (!LOCK_MAP.containsKey(event.getKey())){
				LOCK_MAP.put(event.getKey(), event);
				log.info("加锁: [{}], [{}]", event.getKey(), LOCK_MAP);
			}
		}else{
			// 移除锁
			if (LOCK_MAP.containsKey(event.getKey())){
				Object remove = LOCK_MAP.remove(event.getKey());
				log.info("解锁: [{}], [{}]", event.getKey(), remove);
			}
		}
	}
}
