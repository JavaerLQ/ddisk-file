package io.ddisk.service.impl;

import io.ddisk.service.EmailService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/12
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Value("${spring.mail.username}")
	private String from;

	@Autowired
	private JavaMailSender mailSender;

	/**
	 * 发送简单文本的邮件方法
	 *
	 * @param to
	 * @param subject
	 * @param content
	 */
	@Override
	public void sendSimpleMail(String to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(content);

		mailSender.send(message);
		log.info("邮件发送成功: from[{}], to[{}], subject[{}], content[{}]", from, to, subject, content);
	}

	/**
	 * 发送HTML邮件的方法
	 *
	 * @param to
	 * @param subject
	 * @param content
	 */
	@Override
	public void sendHtmlMail(String to, String subject, String content){
		MimeMessage message = mailSender.createMimeMessage();
		Try.run(()->{
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setFrom(from);
			helper.setSubject(subject);
			helper.setText(content, true);
			mailSender.send(message);
			log.info("邮件发送成功: from[{}], to[{}], subject[{}], content[{}]", from, to, subject, content);
		});
	}
}
