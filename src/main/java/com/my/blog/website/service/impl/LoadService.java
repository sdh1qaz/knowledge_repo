package com.my.blog.website.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.my.blog.website.dao.ContentVoMapper;
import com.my.blog.website.dao.OptionVoMapper;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.utils.CMDUtil;

/**
 * @ClassName： LoadService
 * @Author: dhSu
 * @Description: 启动项
 * @Date:Created in 2019年5月8日
 */

@Service
public class LoadService implements CommandLineRunner{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadService.class);

	@Resource
	private HistoryQueue<ContentVo> histQ;
	
	@Autowired
	private OptionVoMapper optionVoDao;
	
	@Autowired
	private ContentVoMapper contentVoDao;
	
	@Override
	public void run(String... arg0) throws Exception {
		//启动时加载数据库中的浏览历史cid到内存中
		loadScanHistory();
		//启动时检测ES和logstash进程，不存在就创建
		checkEsAndLogstash();
		
	}
	
	/**
	 * 启动时加载数据库中的浏览历史cid到内存中
	 */
	public void loadScanHistory() {
		LOGGER.info("启动项：加载数据库中的浏览历史cid到内存开始。。。");
		//读取数据库中的浏览历史cid
		OptionVo op = optionVoDao.selectByPrimaryKey("history_cid_list");
		String[] cArr = op.getValue().split("-");
		List<ContentVo> opList = new ArrayList<>();
		for(String cid : cArr) {
			//获取文章对象
			ContentVo cont = contentVoDao.selectByPrimaryKey(Integer.valueOf(cid));
			if (cont != null) {
				opList.add(cont);
			}
		}
		//文章列表添加队列
		histQ.addAll(opList);
		LOGGER.info("启动项：加载数据库中的浏览历史cid到内存结束。。。");
	}
	
	/**
	 * 启动时检测ES和logstash进程，不存在就创建
	 * @throws InterruptedException
	 */
	public void checkEsAndLogstash() throws InterruptedException {
		//如果es没有启动，启动es
		String netstat = CMDUtil.excuteCMDCommand("netstat -ano").replaceAll(" ", "");
		// \s*代表若干个空格
		if (!netstat.contains("92000.0.0.0:0LISTENING")) {
			//启动es和logstash
			LOGGER.info("启动项：未检测到es进程，开始启动ES5.4.3，脚本地址：D:\\elasticsearch-5.4.3\\bin\\elasticsearch.bat...");
			String bat_e = "D:\\elasticsearch-5.4.3\\bin\\elasticsearch.bat";
			CMDUtil.runbat(bat_e);
			Thread.sleep(3000);
		}else {
			LOGGER.info("启动项：检测到es进程已经存在，不再启动...");
		}
		
		//如果logstash没有启动，启动logstash
		/*String jps = CMDUtil.excuteCMDCommand("jps -v");
		if (!jps.contains("logstash-5.4.3")) {
			LOGGER.info("启动项：未检测到logstash进程，开始启动logstash5.4.3，脚本地址：D:\\logstash-5.4.3\\bin\\start_logstash.bat");
			String bat_l = "D:\\logstash-5.4.3\\bin\\start_logstash.bat";
			CMDUtil.runbat(bat_l);
		}else {
			LOGGER.info("启动项：检测到logstash进程已经存在，不再启动...");
		}*/
	}
}
