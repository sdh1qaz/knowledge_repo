package com.my.blog.website.es;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.model.Vo.ContentVo;


@Controller
public class ESArticleController extends BaseController{
	
	@Autowired
	ArticleDao articleDao;
	
	/**
	 * 单个字段查询，title匹配的会搜出来
	 */
	/*@RequestMapping("/search/single/{title}")
	public List<ArticleEntity> singleQuery(@PathVariable String title)  {
		List<ArticleEntity> list = articleDao.findByTitle(title);
		return list;
	}*/
	
	/**
	 * 2个字段联合查询,and，title和content都匹配的会搜出来
	 */
	/*@RequestMapping("/search/multiand/{key}")
	public List<ArticleEntity> jointQuery(@PathVariable String key)  {
		List<ArticleEntity> list = articleDao.getByTitleAndContent(key,key);
		return list;
	}*/
	
	/**
	 * 2个字段联合查询,or关系，title匹配或者content匹配的会搜出来
	 */
	@RequestMapping("/search/multior/{key}")
	public String jointOrQuery(@PathVariable String key,HttpServletRequest request)  {
		List<ArticleEntity> list = articleDao.getByTitleOrContent(key,key);
		List<ContentVo> contentVos = new ArrayList<>();
		for(ArticleEntity articleEntity : list) {
			ContentVo contentVo = new ContentVo();
			contentVo.setCid(Integer.valueOf(articleEntity.getCid()));
			contentVo.setContent(articleEntity.getContent());
			contentVo.setHits(Integer.valueOf(articleEntity.getHits()));
			contentVo.setTitle(articleEntity.getTitle());
			contentVos.add(contentVo);
		}
		PageInfo<ContentVo> articles = new PageInfo<>(contentVos);
		request.setAttribute("articles", articles);
		return this.render("search_result");
	}
}
