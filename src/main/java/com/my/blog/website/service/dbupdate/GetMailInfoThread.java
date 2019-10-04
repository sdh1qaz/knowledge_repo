package com.my.blog.website.service.dbupdate;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 用于获取多个邮件信息的线程
 * @author SuDenghui
 */
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 获取邮箱中邮件信息
 * 
 * @author lupf
 *
 */
public class GetMailInfoThread extends Thread {
	Message message[] = null;
	MailImfo re = null;

	public GetMailInfoThread(Message message[]) {
		this.message = message;
	}

	@Override
	public void run() {
		super.run();
		if (null != message) {
			int len = message.length;
			for (int i = len; i > 0; i--) {
				try {
					re = new MailImfo((MimeMessage) message[i - 1]);
					//如果邮件主题包含“小书包数据同步”字样，则读取
					if (re.getSubject().contains(MailConstants.subject)) {
						//需要的内容，sql
						/*String[] sql = re.getBodyText().split("$$$");
						System.out.println(sql[0]+"***********************");*/
						int end = re.getBodyText().indexOf("<html>");
						String sql = re.getBodyText().substring(0, end);
						//将sql写到文件E://tale.sql
						File f = new File(MailConstants.sqlFile);
						if (f.exists()) {
							f.delete();
							f.createNewFile();
						}
						FileOutputStream fout = new FileOutputStream(f);
						fout.write(sql.getBytes());
						fout.close();
						//执行sql脚本
						OperateDB.runSqlFile(MailConstants.sqlFile);
						break;
					}
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
