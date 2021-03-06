package com.my.blog.website.dao;

import com.my.blog.website.model.Bo.ArchiveBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ContentVoMapper {
	long countByExample(ContentVoExample example);

	int deleteByExample(ContentVoExample example);

	int deleteByPrimaryKey(Integer cid);

	int insert(ContentVo record);

	int insertSelective(ContentVo record);

	List<ContentVo> selectByExampleWithBLOBs(ContentVoExample example);

	List<ContentVo> selectByExample(ContentVoExample example);

	ContentVo selectByPrimaryKey(Integer cid);

	int updateByExampleSelective(@Param("record") ContentVo record, @Param("example") ContentVoExample example);

	int updateByExampleWithBLOBs(@Param("record") ContentVo record, @Param("example") ContentVoExample example);

	int updateByExample(@Param("record") ContentVo record, @Param("example") ContentVoExample example);

	int updateByPrimaryKeySelective(ContentVo record);

	int updateByPrimaryKeyWithBLOBs(ContentVo record);

	int updateByPrimaryKey(ContentVo record);

	List<ArchiveBo> findReturnArchiveBo();

	List<ContentVo> findByCatalog(Integer mid);

	/**
	 * @Description: 查询最近添加的num篇文章
	 */
	List<ContentVo> getNewlyArticles(Integer num);

	// 文章的点击数加1
	int hitsAddsByOne(Integer cid);

	// 根据名字搜文章
	List<ContentVo> getCont(String title);

	/**
	 * 更新文章的最近阅读时间
	 */
	int updateArticleReadtime(Integer cid);

}