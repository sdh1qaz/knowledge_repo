package com.my.blog.website.service.dbupdate;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeBagUpdateUtil {
	
	/**
	 * 本地数据库有更新，发送邮件
	 */
	public static void sendLocal() {
		SendQQemailByJava.SendEmail();
	}
	
	/**
	 * 更新本地数据库
	 */
	public static void updateLocal() {
		MailRead.updateXSB();
	}
	
	public static void main(String[] args) {
		updateLocal();
	}
}
