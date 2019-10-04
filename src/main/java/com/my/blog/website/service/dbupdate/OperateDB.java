package com.my.blog.website.service.dbupdate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperateDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(OperateDB.class);
	
	/**
	 * 导出数据库
	 */
	public static String  exportDataSql() {
		String outStr;
		try {
			Runtime rt = Runtime.getRuntime();
			// 调用 调用mysql的安装目录的命令
			Process child = rt.exec(MailConstants.mysqlAddr + "\\bin\\mysqldump "
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
			outStr = "Dump数据库发生了异常,错误信息：" + e.getMessage();
			LOGGER.error("Dump数据库出错",e);
		}
		return outStr;
	}
	
	/**
	 * 更新数据库
	 * 运行指定的sql脚本
	 * @param sqlFileName 如"E://tale.sql"
	 */
	public static void runSqlFile(String sqlFileName) {
		try {
			// 建立连接
			Connection conn = DriverManager.getConnection(MailConstants.db_url, 
					MailConstants.db_username, MailConstants.db_pwd);
			// 创建ScriptRunner，用于执行SQL脚本
			ScriptRunner runner = new ScriptRunner(conn);
			runner.setErrorLogWriter(null);
			runner.setLogWriter(null);
			// 执行SQL脚本
			runner.runScript(new InputStreamReader(new FileInputStream(sqlFileName), "utf-8"));
			// 关闭连接
			conn.close();
			// 若成功，打印提示信息
			LOGGER.info("====== 数据库更新完成 ======");
		} catch (IOException | SQLException e) {
			LOGGER.error("数据库更新出错：" + e.getMessage());
		}
	}
	
}
