package com.my.blog.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import com.my.blog.website.utils.CMDUtil;

/**
 * 小书包运行前启动项
 * @author 苏登辉
 */
@Service
public class StartUpItemsService implements CommandLineRunner{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StartUpItemsService.class);
	
	@Override
	public void run(String... args) throws Exception {
		//启动es和logstash
		LOGGER.info("开始启动ES5.4.3，脚本地址：D:\\elasticsearch-5.4.3\\bin\\elasticsearch.bat");
		String bat_e = "D:\\elasticsearch-5.4.3\\bin\\elasticsearch.bat";
		CMDUtil.runbat(bat_e);
		Thread.sleep(3000);
		
		LOGGER.info("开始启动logstash5.4.3，脚本地址：D:\\logstash-5.4.3\\bin\\start_logstash.bat");
		String bat_l = "D:\\logstash-5.4.3\\bin\\start_logstash.bat";
		CMDUtil.runbat(bat_l);
	}
	
}
