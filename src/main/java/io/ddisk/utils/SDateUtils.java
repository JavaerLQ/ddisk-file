package io.ddisk.utils;

import java.util.Date;
import java.util.Objects;

/**
 * 安全的时间工具类
 * @Author: Richard.Lee
 * @Date: created by 2021/5/3
 */
public class SDateUtils {

	/**
	 * 判断现在是否在thatDate之前, 当thatDate为null时，代表无穷大
	 * @param thatDate
	 * @return
	 */
	public static Boolean nowBefore(Date thatDate){
		Date now = new Date();
		if (Objects.isNull(thatDate)){
			return true;
		}
		return now.before(thatDate);
	}

	/**
	 * 判断现在是否在thatDate之后, 当thatDate为null时，代表无穷大
	 * @param thatDate
	 * @return
	 */
	public static Boolean nowAfter(Date thatDate){
		Date now = new Date();
		if (Objects.isNull(thatDate)){
			return false;
		}
		return now.before(thatDate);
	}
}
