package io.ddisk.eventbus.event;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/20
 */
@Data
public abstract class CrazyEvent implements Serializable {

	/**
	 * 事件代码
	 */
	private String eventCode = UUID.randomUUID().toString();

	/**
	 * 创建时间
	 */
	private Date createTime = new Date();

	/**
	 * 延迟时间
	 */
	private long delay = 0L;

	public Date getDueDate(){
		long ms = createTime.getTime() + delay;
		return new Date(ms);
	}
}