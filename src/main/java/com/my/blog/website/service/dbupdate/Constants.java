package com.my.blog.website.service.dbupdate;

import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.io.Resources;

/**
 * 常量
 */
public class Constants {
	public static String subject;//邮件主题
	public static String sqlFile;//读取邮件的sql内容后保存到的路径，如E://tale.sql
	public static String readEmail;//要读取的qq邮箱,如348673242@qq.com
	public static String sendEmail;//发件人qq邮箱地址,如348673242@qq.com
	public static String sendName;//发件人姓名
	public static String authpwd;//授权码
	public static String mysqlAddr;//本地mysql安装目录，E:\mysql-5.6.24-win32
	public static String db_url;//jdbc:mysql://localhost:3306/tale?characterEncoding=UTF-8
	public static String db_username;//用户名，root
	public static String db_pwd;//密码，admin
	
	
	
	static {
		 
		try {
			Properties props = Resources.getResourceAsProperties("updateKnowledgeBag.properties");
			subject = props.getProperty("subject"); 
			sqlFile = props.getProperty("sqlFile"); 
			readEmail = props.getProperty("readEmail"); 
			sendEmail = props.getProperty("sendEmail"); 
			sendName = props.getProperty("sendName"); 
			authpwd = props.getProperty("authpwd"); 
			mysqlAddr = props.getProperty("mysqlAddr"); 
			db_url = props.getProperty("db_url"); 
			db_username = props.getProperty("db_username"); 
			db_pwd = props.getProperty("db_pwd"); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
