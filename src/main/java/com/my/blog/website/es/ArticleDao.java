package com.my.blog.website.es;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * es增删改查
 * @author 苏登辉
 */
public interface ArticleDao extends CrudRepository<ArticleEntity,String>{
	
	public List<ArticleEntity> getByContent(String cont);
	public List<ArticleEntity> findByTitle(String title);
	public List<ArticleEntity> getByTitleAndContent(String title, String content);
	public List<ArticleEntity> getByTitleOrContent(String title, String content);
}
