package com.my.blog.website.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 反序列化ES返回的结果，对应es中的一个文档
 * @author 苏登辉
 */

//@Document表示这是一个Elastic Data,indexName和type对应es中的index（索引）和type（类型）
@Document(indexName="knowledge_bag",type="article")
public class ArticleEntity {
	
	@Id//声明文档的主键
	private String id;//文章cid
	private String title;//文章标题
	private String content;//文章内容
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
