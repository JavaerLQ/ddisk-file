package io.ddisk.service;

import javax.mail.MessagingException;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/12
 */
public interface EmailService {
	/**
	 * 发送简单文本的邮件方法
	 * @param to
	 * @param subject
	 * @param content
	 */
	void sendSimpleMail(String to,String subject,String content);

	/**
	 * 发送HTML邮件的方法
	 * @param to
	 * @param subject
	 * @param content
	 */
	void sendHtmlMail(String to ,String subject,String content) throws MessagingException;
}
