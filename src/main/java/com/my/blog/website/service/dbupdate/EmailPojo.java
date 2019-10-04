package com.my.blog.website.service.dbupdate;

import java.io.Serializable;

/**
 * 封装邮件服务器、端口、发件人信息、授权码等信息
 * @author 苏登辉
 */
public class EmailPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -272551366607796259L;
	
	
	private String hostname;//邮件服务器
	private int port;//邮件服务器端口
	private String sendEmail;//发件人邮箱地址
	private String sendName;//发件人姓名
	private String receiveEmail;//收件邮箱地址
	private String auth;//授权码，qq邮箱使用第三方客户端收发邮件时需要授权码
	private String subJect;//邮件主题
	private String msg;//邮件内容
	
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getSendEmail() {
		return sendEmail;
	}
	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}
	
	public String getSendName() {
		return sendName;
	}
	public void setSendName(String sendName) {
		this.sendName = sendName;
	}
	public String getReceiveEmail() {
		return receiveEmail;
	}
	public void setReceiveEmail(String receiveEmail) {
		this.receiveEmail = receiveEmail;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getSubJect() {
		return subJect;
	}
	public void setSubJect(String subJect) {
		this.subJect = subJect;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "EmailPojo [hostname=" + hostname + ", port=" + port + ", sendEmail=" + sendEmail + ", sendName="
				+ sendName + ", receiveEmail=" + receiveEmail + ", auth=" + auth + ", subJect=" + subJect + ", msg="
				+ msg + "]";
	}
	
}
