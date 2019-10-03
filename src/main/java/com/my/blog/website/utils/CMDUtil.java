package com.my.blog.website.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行windows的cmd命令工具类
 * @author 苏登辉
 */

public class CMDUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CMDUtil.class);
	
	/**
	 * 执行一个cmd命令
	 * @param cmdCommand cmd命令
	 * @return 命令执行结果字符串，如出现异常返回null
	 */
	public static String excuteCMDCommand(String cmdCommand) {
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		//System.out.println(excuteCMDCommand("netstat -ano"));
		//System.out.println(excuteCMDCommand("jps -v"));
		String cc = excuteCMDCommand("netstat -ano").replaceAll(" ", "");
		System.out.println(cc.contains("92000.0.0.0:0LISTENING"));
	}

	/**
	 * 执行bat文件，
	 * @param file bat文件路径
	 * @param isCloseWindow 执行完毕后是否关闭cmd窗口
	 * @return bat文件输出log
	 */
	public static String excuteBatFile(String file, boolean isCloseWindow) {
		String cmdCommand = null;
		if (isCloseWindow) {
			cmdCommand = "cmd.exe /c " + file;
		} else {
			cmdCommand = "cmd.exe /k " + file;
		}
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdCommand);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 执行bat文件
	 */
	public static void runbat(String batPath) {
		//如，D:\elasticsearch-5.4.3\bin\elasticsearch.bat
		if (StringUtil.isNullOrEmpry(batPath)) {
			LOGGER.info("bat脚本路径为空。。。");
			return;
		}
		try {
			Runtime.getRuntime().exec("cmd.exe /C start " + batPath);
		} catch (IOException e) {
			LOGGER.error("执行" + batPath + "出错：" + e);
		}

	}

}
