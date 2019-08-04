package com.my.blog.website.es;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ESArticleController {
	
	@Autowired
	ArticleDao articleDao;
	
	/**
	 * 单个字段查询，title匹配的会搜出来
	 */
	@RequestMapping("/search/single/{title}")
	public List<ArticleEntity> singleQuery(@PathVariable String title)  {
		List<ArticleEntity> list = articleDao.findByTitle(title);
		return list;
	}
	
	/**
	 * 2个字段联合查询,and，title和content都匹配的会搜出来
	 */
	@RequestMapping("/search/multiand/{key}")
	public List<ArticleEntity> jointQuery(@PathVariable String key)  {
		
		List<ArticleEntity> list = articleDao.getByTitleAndContent(key,key);
		return list;
	}
	
	/**
	 * 2个字段联合查询,or关系，title匹配或者content匹配的会搜出来
	 */
	@RequestMapping("/search/multior/{key}")
	public List<ArticleEntity> jointOrQuery(@PathVariable String key)  {
		
		List<ArticleEntity> list = articleDao.getByTitleOrContent(key,key);
		return list;
	}
}
