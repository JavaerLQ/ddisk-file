package io.ddisk.utils;

import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;

import java.lang.reflect.Method;
import java.util.UUID;


/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/22
 */
public class UUIDUtil {

	/**
	 * 随机生成32位UUID
	 * @return
	 */
	public static String random32() {

		try{
			Method method = Long.class.getDeclaredMethod("formatUnsignedLong0", long.class, int.class, byte[].class, int.class, int
			.class);
			method.setAccessible(true);

			UUID uuid = UUID.randomUUID();
			long lsb = uuid.getLeastSignificantBits();
			long msb = uuid.getMostSignificantBits();

			byte[] buf = new byte[32];
			method.invoke(Long.class, lsb, 4, buf, 20, 12);
			method.invoke(Long.class, lsb >>> 48, 4, buf, 16, 4);
			method.invoke(Long.class, msb, 4, buf, 12, 4);
			method.invoke(Long.class, msb >>> 16, 4, buf, 8,  4);
			method.invoke(Long.class, msb >>> 32, 4, buf, 0,  8);
			return new String(buf);
		}catch (Throwable throwable){
			throw new BizException(BizMessage.SYSTEM_UUID_ERROR, throwable);
		}
	}
}
