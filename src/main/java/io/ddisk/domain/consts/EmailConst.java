package io.ddisk.domain.consts;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/12
 */
public class EmailConst {


	/**
	 *  用户修改密码邮件主题
	 */
	public static final String RESET_PASSWD_SUBJECT = "密码重置";

	/**
	 * 用户注册邮件主题
	 */
	public static final String USER_REGISTER_SUBJECT = "用户注册";

	/**
	 * 用户修改密码成功邮件主题
	 */
	public static final String RESET_PASSWD_SUCCESS_SUBJECT = "密码修改成功";

	/**
	 * 参数1：username, 参数2：分钟，参数3，token;
	 */
	public static final String RESET_PASSWD_HTML_CONTENT = "亲爱的%s!<p>这是一封密码重置邮件，令牌有效时间为%d分钟。</p> <p><b>%s</b></p><p>如果您并没有执行此操作，您可以选择忽略此邮件。</p>";

	/**
	 * 参数1：username, 参数2：passwd;
	 */
	public static final String RESET_PASSWD_SUCCESS_HTML_CONTENT = "亲爱的%s!<p>您的密码修改成功，新密码：</p> <p><b>%s</b></p><p>请勿将密码泄露给他人。</p>";

	/**
	 * 参数1：分钟，参数2：token;
	 */
	public static final String USER_REGISTER_HTML_CONTENT = "亲爱的用户!<p>这是一封用户注册邮件，令牌有效时间为%d分钟。</p> <p><b>%s</b></p><p>如果您并没有执行此操作，您可以选择忽略此邮件。</p>";

}
