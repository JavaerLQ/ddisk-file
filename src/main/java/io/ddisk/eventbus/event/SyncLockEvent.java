package io.ddisk.eventbus.event;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SyncLockEvent extends CrazyEvent{

	/**
	 * 存储锁对象
	 */
	public final static Map<Object, Object> LOCK_MAP = Maps.newConcurrentMap();

	private Object key;
	private Boolean unlock = false;

	/**
	 * @param key 锁对象
	 * @param delay 延迟时间
	 */
	public SyncLockEvent(Object key, long delay){
		this(key, false, delay);
	}


	public SyncLockEvent(Object key){
		this(key, false, 0L);
	}

	public SyncLockEvent(Object key, Boolean unlock){
		this(key, unlock, 0L);
	}

	public SyncLockEvent(Object key, Boolean unlock, long delay){
		super();
		this.setDelay(delay);
		this.unlock = unlock;
		this.key = Optional.ofNullable(key).orElse(this.getClass());
	}

	public static Object getLock(Object key){
		return LOCK_MAP.getOrDefault(key, SyncLockEvent.class);
	}
}
