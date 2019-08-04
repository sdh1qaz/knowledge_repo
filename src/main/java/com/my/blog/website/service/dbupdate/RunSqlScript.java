package com.my.blog.website.service.dbupdate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 运行Sql脚本 sql脚本放在resources下的sql文件夹下
 */
public final class RunSqlScript {
	private static final Logger LOGGER = LoggerFactory.getLogger(RunSqlScript.class);
	
	/**
	 * 运行指定的sql脚本
	 * @param sqlFileName 如"E://tale.sql"
	 */
	public static void runSqlFile(String sqlFileName) {
		try {
			// 建立连接
			Connection conn = DriverManager.getConnection(Constants.db_url, 
					Constants.db_username, Constants.db_pwd);
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