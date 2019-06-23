package com.my.blog.website.service.dbupdate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OperateDataBySql {
	
	
	/**
	 * 实现数据库的导出（方法2）
	 */
	public static String  exportDataSql() {
		String outStr;
		try {
			Runtime rt = Runtime.getRuntime();
			// 调用 调用mysql的安装目录的命令
			Process child = rt.exec(Constants.mysqlAddr + "\\bin\\mysqldump "
					+ "-uroot -padmin tale");
			// 设置导出编码为utf-8。这里必须是utf-8
			// 把进程执行中的控制台输出信息写入.sql文件，即生成了备份文件。注：如果不对控制台信息进行读出，则会导致进程堵塞无法运行
			InputStream in = child.getInputStream();// 控制台的输出信息作为输入流
			InputStreamReader xx = new InputStreamReader(in, "utf-8");
			// 设置输出流编码为utf-8。这里必须是utf-8，否则从流中读入的是乱码
			String inStr;
			StringBuffer sb = new StringBuffer("");
			// 组合控制台输出信息字符串
			BufferedReader br = new BufferedReader(xx);
			while ((inStr = br.readLine()) != null) {
				sb.append(inStr + "\r\n");
			}
			outStr = sb.toString();
			in.close();
			xx.close();
			br.close();
			return outStr;
		} catch (Exception e) {
			e.printStackTrace();
			outStr = "发生了异常";
		}
		return outStr;
	}
}
