package com.my.blog.website.service.dbupdate;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮箱信息常量
 */
@Component
@ConfigurationProperties(prefix = "db.update")
public class MailConstants {

	public static String subject;// 邮件主题
	public static String sqlFile;// 读取邮件的sql内容后保存到的路径，如E://tale.sql
	public static String readEmail;// 要读取的qq邮箱,如348673242@qq.com
	public static String sendEmail;// 发件人qq邮箱地址,如348673242@qq.com
	public static String sendName;// 发件人姓名
	public static String authpwd;// 授权码
	public static String mysqlAddr;// 本地mysql安装目录，E:\mysql-5.6.24-win32
	public static String db_url;// jdbc:mysql://localhost:3306/tale?characterEncoding=UTF-8
	public static String db_username;// 用户名，root
	public static String db_pwd;// 密码，admin

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		MailConstants.subject = subject;
	}

	public String getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(String sqlFile) {
		MailConstants.sqlFile = sqlFile;
	}

	public String getReadEmail() {
		return readEmail;
	}

	public void setReadEmail(String readEmail) {
		MailConstants.readEmail = readEmail;
	}

	public String getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(String sendEmail) {
		MailConstants.sendEmail = sendEmail;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		MailConstants.sendName = sendName;
	}

	public String getAuthpwd() {
		return authpwd;
	}

	public void setAuthpwd(String authpwd) {
		MailConstants.authpwd = authpwd;
	}

	public String getMysqlAddr() {
		return mysqlAddr;
	}

	public void setMysqlAddr(String mysqlAddr) {
		MailConstants.mysqlAddr = mysqlAddr;
	}

	public String getDb_url() {
		return db_url;
	}

	public void setDb_url(String db_url) {
		MailConstants.db_url = db_url;
	}

	public String getDb_username() {
		return db_username;
	}

	public void setDb_username(String db_username) {
		MailConstants.db_username = db_username;
	}

	public String getDb_pwd() {
		return db_pwd;
	}

	public void setDb_pwd(String db_pwd) {
		MailConstants.db_pwd = db_pwd;
	}

}
