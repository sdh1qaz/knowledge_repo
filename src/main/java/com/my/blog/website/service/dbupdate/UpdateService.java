package com.my.blog.website.service.dbupdate;

import java.io.File;
import java.io.FileOutputStream;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

	/**
	 * 本地数据库备份，发送邮件,异步方法
	 */
	@Async
	public void sendLocal() {
		ReadSendQQemail.SendEmail();
	}

	/**
	 * 更新 读取邮件中sql，更新本地数据库，异步方法
	 */
	@Async
	public void updateLocal() {
		Message[] message = ReadSendQQemail.readEamil();
		if (message == null) {
			return;
		}
		MailInfo mailInfo = null;
		int len = message.length;
		for (int i = len; i > 0; i--) {
			try {
				mailInfo = new MailInfo((MimeMessage) message[i - 1]);
				// 如果邮件主题包含“konwledge-bag-update”字样，则读取
				if (mailInfo.getSubject().contains(MailConstants.subject)) {
					// 读取邮件中的内容-sql
					int end = mailInfo.getBodyText().indexOf("<html>");
					String sql = mailInfo.getBodyText().substring(0, end);
					// 将sql写到文件E://tale.sql
					File f = new File(MailConstants.sqlFile);
					if (f.exists()) {
						f.delete();
						f.createNewFile();
					}
					FileOutputStream fout = new FileOutputStream(f);
					fout.write(sql.getBytes());
					fout.close();
					// 执行sql脚本
					OperateDB.runSqlFile(MailConstants.sqlFile);
					break;
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
