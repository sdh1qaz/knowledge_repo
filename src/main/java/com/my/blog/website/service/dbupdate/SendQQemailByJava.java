package com.my.blog.website.service.dbupdate;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java操作qq邮箱收发邮件
 * 
 * @author SuDenghui
 */
public class SendQQemailByJava {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendQQemailByJava.class);

	/**
	 * 本地本分数据上传，发送邮件
	 */
	public static void SendEmail() {
		sendEmail(OperateDB.exportDataSql());
	}
	
	@Test
	public void testSendEmail() {
		sendEmail(OperateDB.exportDataSql());
	}
	
	/*
	 * 发送邮件
	 */
	public  static void sendEmail(String msg) {
		EmailPojo ep = new EmailPojo();
		ep.setHostname("smtp.qq.com");
		ep.setPort(465);
		ep.setSendEmail(MailConstants.sendEmail);
		ep.setReceiveEmail(MailConstants.readEmail);
		ep.setAuth(MailConstants.authpwd);
		ep.setSubJect(MailConstants.subject);
		ep.setSendName(MailConstants.sendName);
		ep.setMsg(msg);
		sendEmail(ep);
	}
	
	/*
	 * 发送邮件
	 */
	public  static void sendEmail(String msg,String subject) {
		EmailPojo ep = new EmailPojo();
		ep.setHostname("smtp.qq.com");
		ep.setPort(465);
		ep.setSendEmail(MailConstants.sendEmail);
		ep.setReceiveEmail(MailConstants.readEmail);
		ep.setAuth(MailConstants.authpwd);
		ep.setSubJect(subject);
		ep.setSendName(MailConstants.sendName);
		ep.setMsg(msg);
		sendEmail(ep);
	}
	
	/*
	 * 发送邮件
	 */
	public static void sendEmail(EmailPojo emailPojo) {
		// 不要使用SimpleEmail,会出现乱码问题
		HtmlEmail email = new HtmlEmail();
		try {
			// 这里是SMTP发送服务器的名字：，普通qq号只能是smtp.qq.com ；smtp.exmail.qq.com没测试成功
			email.setHostName(emailPojo.getHostname());//"smtp.qq.com"
			// 设置需要鉴权端口
			email.setSmtpPort(emailPojo.getPort());//465
			// 开启 SSL 加密
			email.setSSLOnConnect(true);
			// 字符编码集的设置
			email.setCharset("utf-8");
			// 收件人的邮箱
			email.addTo(emailPojo.getReceiveEmail());//"dhsu2@iflytek.com"
			// 发送人的邮箱
			email.setFrom(emailPojo.getSendEmail(),emailPojo.getSendName());//"348673242@qq.com", "苏登辉"
			// 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和得到的授权码
			//"348673242@qq.com", "racrnqyoeglnbhea"
			email.setAuthentication(emailPojo.getSendEmail(),emailPojo.getAuth());
			email.setSubject(emailPojo.getSubJect());//"下午3：00会议室讨论，请准时参加"
			// 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签
			email.setMsg(emailPojo.getMsg());//"邮件内容"
			// 发送
			email.send();
			LOGGER.info("邮件发送成功!");
		} catch (EmailException e) {
			LOGGER.error("邮件发送失败!",e);
		}
	}
}
