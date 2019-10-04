package com.my.blog.website.service.dbupdate;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java操作qq邮箱收发邮件
 * @author 苏登辉
 */
public class ReadSendQQemail {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadSendQQemail.class);

	/**
	 * 发送QQ邮件
	 */
	public static void SendEmail() {
		sendEmail(OperateDB.exportDataSql());
	}

	@Test
	public void testSendEmail() {
		sendEmail(OperateDB.exportDataSql());
	}

	/**
	 * 发送QQ邮件-只有内容，没有主题
	 * @param msg
	 */
	public static void sendEmail(String msg) {
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

	/**
	 * 发送QQ邮件-内容和主题都有，主题自己设定
	 * @param msg
	 * @param subject
	 */
	public static void sendEmail(String msg, String subject) {
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

	/**
	 * 发送QQ邮件-EmailPojo
	 * @param emailPojo
	 */
	public static void sendEmail(EmailPojo emailPojo) {
		// 不要使用SimpleEmail,会出现乱码问题
		HtmlEmail email = new HtmlEmail();
		try {
			// 这里是SMTP发送服务器的名字：，普通qq号只能是smtp.qq.com ；smtp.exmail.qq.com没测试成功
			email.setHostName(emailPojo.getHostname());// "smtp.qq.com"
			// 设置需要鉴权端口
			email.setSmtpPort(emailPojo.getPort());// 465
			// 开启 SSL 加密
			email.setSSLOnConnect(true);
			// 字符编码集的设置
			email.setCharset("utf-8");
			// 收件人的邮箱
			email.addTo(emailPojo.getReceiveEmail());// "dhsu2@iflytek.com"
			// 发送人的邮箱
			email.setFrom(emailPojo.getSendEmail(), emailPojo.getSendName());// "348673242@qq.com", "苏登辉"
			// 如果需要认证信息的话，设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和得到的授权码
			// "348673242@qq.com", "racrnqyoeglnbhea"
			email.setAuthentication(emailPojo.getSendEmail(), emailPojo.getAuth());
			email.setSubject(emailPojo.getSubJect());// "下午3：00会议室讨论，请准时参加"
			// 要发送的信息，由于使用了HtmlEmail，可以在邮件内容中使用HTML标签
			email.setMsg(emailPojo.getMsg());// "邮件内容"
			// 发送
			email.send();
			LOGGER.info("邮件发送成功!");
		} catch (EmailException e) {
			LOGGER.error("邮件发送失败!", e);
		}
	}
	
	/**
	 * 读取QQ邮件，返回Messgae数组
	 */
	public static Message[] readEamil() {
		try
        {
            String host = "pop.qq.com";
            String username = MailConstants.readEmail;//要读取的邮箱
            String password = MailConstants.authpwd;//授权码

            Properties p = new Properties();
            p.setProperty("mail.pop3.host", host);
            p.setProperty("mail.pop3.port", "995");
            // SSL安全连接参数
            p.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            p.setProperty("mail.pop3.socketFactory.fallback", "true");
            p.setProperty("mail.pop3.socketFactory.port", "995");

            Session session = Session.getDefaultInstance(p, null);
            Store store = session.getStore("pop3");
            store.connect(host, username, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            return messages;
        }
        catch (Exception e)
        {
           LOGGER.error(e.getMessage(),e);
        }
		return null;
	}
}
