package com.my.blog.website.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.my.blog.website.dao.ItemVoMapper;
import com.my.blog.website.dao.OptionVoMapper;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ItemVo;
import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.service.dbupdate.OperateDataBySql;
import com.my.blog.website.service.dbupdate.SendQQemailByJava;

/**
 * @ClassName： SchedulService
 * @Author: dhSu
 * @Description: 定时任务
 * @Date:Created in 2019年5月8日
 */
@Component
@Configurable
@EnableScheduling
public class SchedulService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulService.class);
	
	@Resource
	private ItemVoMapper itemDao;
	
	@Resource
	private HistoryQueue<ContentVo> histQ;
	
	@Autowired
	private OptionVoMapper optionVoDao;
	
	/**
	 * 每1分钟将队列中的文章id插入数据库中
	 */
	@Scheduled(cron = "0 0/1 * * * ? ")
	public void updateQueue() {
		LOGGER.info("定时任务：浏览历史列表中的文章cid更新到数据库开始。。。");
		//遍历队列
		Iterator<ContentVo> iterator = histQ.iterator();
		//历史文章cid的字符串，以-间隔
		StringBuffer cidStr = new StringBuffer();
		while (iterator.hasNext()) {
			ContentVo c = iterator.next();
			/*if (c == null) {
				LOGGER.info("定时任务：浏览历史列表中的文章cid更新到数据库完成。。。");
				return;
			}*/
			cidStr.append(c.getCid() + "-");
		}
		//将cidStr更新到数据库中
		OptionVo op = new OptionVo();
		op.setName("history_cid_list");
		op.setValue(cidStr.toString());
		optionVoDao.updateByPrimaryKeySelective(op);
		LOGGER.info("定时任务：浏览历史列表中的文章cid更新到数据库完成。。。");
	}
	
	/**
	 * 每天24:00数据库备份，生成一个备份文件
	 * @throws IOException 
	 */
	@Scheduled(cron = "0 0 22 * * ?")
	public void backupDBdaily() throws IOException {
		
		LOGGER.info("定时任务：生成数据库备份的sql文件开始。。。");
		//获取sql
		String sql = OperateDataBySql.exportDataSql();
		//保存到文件中
		File f = new File("E:\\小书包数据备份\\tale" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".sql");
		//文件存在则删除
		if (f.exists()) {
			f.delete();
		}
		//重建文件
		f.createNewFile();
		OutputStream outputStream = new FileOutputStream(f);
		outputStream.write(sql.getBytes());
		outputStream.close();
		LOGGER.info("定时任务：生成数据库备份的sql文件完成。。。");
	}
	
	/**
	 * 每天09:20读取代办事项，发送一封邮件到我的qq邮箱
	 * @throws IOException 
	 */
	@Scheduled(cron = "0 20 9 * * ?")
	public void sendItemsByQQMail() throws IOException {
		//获取代办事项
		List<ItemVo> items = itemDao.selectAll();
		StringBuilder sb = new StringBuilder();
		int size = items.size();
		if (size < 1) {
			return;
		}
		for(int i=0;i<size;i++) {
			sb.append((i+1) + "、" + items.get(i).getCont() + "\n");
		}
		LOGGER.info("定时任务：发送代办事项到qq邮箱开始。。。");
		String msg = sb.toString();
		SendQQemailByJava.sendEmail(msg,"代办事项提醒！");
		LOGGER.info("定时任务：发送代办事项到qq邮箱完成。。。");
	}
	
}
