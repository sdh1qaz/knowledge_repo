package com.my.blog.website.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dao.ContentVoMapper;
import com.my.blog.website.dao.MetaVoMapper;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.KnowledgeRepoException;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.IRelationshipService;
import com.my.blog.website.utils.DateKit;
import com.my.blog.website.utils.TaleUtils;
import com.my.blog.website.utils.Tools;
import com.vdurmont.emoji.EmojiParser;

/**
 * Created by Administrator on 2017/3/13 013.
 */
@Service
public class ContentServiceImpl implements IContentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

	@Resource
	private ContentVoMapper contentDao;

	@Resource
	private MetaVoMapper metaDao;

	@Resource
	private IRelationshipService relationshipService;

	@Resource
	private IMetaService metasService;

	@Override
	@Transactional
	public String publish(ContentVo contents) {
		if (null == contents) {
			return "文章对象为空";
		}
		if (StringUtils.isBlank(contents.getTitle())) {
			return "文章标题不能为空";
		}
		if (StringUtils.isBlank(contents.getContent())) {
			return "文章内容不能为空";
		}
		int titleLength = contents.getTitle().length();
		if (titleLength > WebConst.MAX_TITLE_COUNT) {
			return "文章标题过长";
		}
		int contentLength = contents.getContent().length();
		if (contentLength > WebConst.MAX_TEXT_COUNT) {
			return "文章内容过长";
		}
		if (null == contents.getAuthorId()) {
			return "请登录后发布文章";
		}
		if (StringUtils.isNotBlank(contents.getSlug())) {
			if (contents.getSlug().length() < 5) {
				return "路径太短了";
			}
			if (!TaleUtils.isPath(contents.getSlug()))
				return "您输入的路径不合法";
			ContentVoExample contentVoExample = new ContentVoExample();
			contentVoExample.createCriteria().andTypeEqualTo(contents.getType()).andStatusEqualTo(contents.getSlug());
			long count = contentDao.countByExample(contentVoExample);
			if (count > 0)
				return "该路径已经存在，请重新输入";
		} else {
			contents.setSlug(null);
		}

		contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

		int time = DateKit.getCurrentUnixTime();
		contents.setCreated(time);
		contents.setModified(time);
		contents.setHits(0);
		contents.setCommentsNum(0);

		String tags = contents.getTags();
		String categories = contents.getCategories();
		contentDao.insert(contents);
		Integer cid = contents.getCid();
		metasService.saveMetas(cid, tags, Types.TAG.getType());
		metasService.saveMetas(cid, categories, Types.CATEGORY.getType());
		return WebConst.SUCCESS_RESULT;
	}

	@Override
	public PageInfo<ContentVo> getContents(Integer p, Integer limit) {
		LOGGER.debug("Enter getContents method");
		ContentVoExample example = new ContentVoExample();
		example.setOrderByClause("hits desc");
		example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType()).andStatusEqualTo(Types.PUBLISH.getType());
		PageHelper.startPage(p, limit);
		List<ContentVo> data = contentDao.selectByExampleWithBLOBs(example);
		PageInfo<ContentVo> pageInfo = new PageInfo<>(data);
		LOGGER.debug("Exit getContents method");
		return pageInfo;
	}

	@Override
	public ContentVo getContents(String id) {
		if (StringUtils.isNotBlank(id)) {
			if (Tools.isNumber(id)) {
				ContentVo contentVo = contentDao.selectByPrimaryKey(Integer.valueOf(id));
				return contentVo;
			} else {
				ContentVoExample contentVoExample = new ContentVoExample();
				contentVoExample.createCriteria().andSlugEqualTo(id);
				List<ContentVo> contentVos = contentDao.selectByExampleWithBLOBs(contentVoExample);
				if (contentVos.size() != 1) {
					throw new KnowledgeRepoException("query content by id and return is not one");
				}
				return contentVos.get(0);
			}
		}
		return null;
	}

	@Override
	public void updateContentByCid(ContentVo contentVo) {
		if (null != contentVo && null != contentVo.getCid()) {
			contentDao.updateByPrimaryKeySelective(contentVo);
		}
	}

	@Override
	public PageInfo<ContentVo> getArticles(Integer mid, int page, int limit) {
		int total = metaDao.countWithSql(mid);
		PageHelper.startPage(page, limit);
		List<ContentVo> list = contentDao.findByCatalog(mid);
		PageInfo<ContentVo> paginator = new PageInfo<>(list);
		paginator.setTotal(total);
		return paginator;
	}

	/**
	 * 搜索、分页
	 * 
	 * @param keyword
	 *            keyword
	 * @param page
	 *            page
	 * @param limit
	 *            limit
	 * @return ContentVo
	 */
	@Override
	public PageInfo<ContentVo> getArticles(String keyword, Integer page, Integer limit) {
		PageHelper.startPage(page, limit);
		ContentVoExample contentVoExample = new ContentVoExample();
		ContentVoExample.Criteria criteria = contentVoExample.createCriteria();
		criteria.andTypeEqualTo(Types.ARTICLE.getType());
		criteria.andStatusEqualTo(Types.PUBLISH.getType());
		criteria.andTitleLike("%" + keyword + "%");
		contentVoExample.setOrderByClause("created desc");
		List<ContentVo> contentVos = contentDao.selectByExampleWithBLOBs(contentVoExample);
		return new PageInfo<>(contentVos);
	}

	/**
	 * 根据关键词搜索文章标题列表
	 * 
	 * @param String
	 *            keyword
	 * @return List<String>
	 */
	public List<String> getTitles(String keyword) {
		ContentVoExample contentVoExample = new ContentVoExample();
		ContentVoExample.Criteria criteria = contentVoExample.createCriteria();
		criteria.andTypeEqualTo(Types.ARTICLE.getType());
		criteria.andStatusEqualTo(Types.PUBLISH.getType());
		criteria.andTitleLike("%" + keyword + "%");
		contentVoExample.setOrderByClause("created desc");
		List<ContentVo> contentVos = contentDao.selectByExampleWithBLOBs(contentVoExample);
		List<String> title = new ArrayList<String>();
		// 遍历contentVos，获取标题列表
		int size = contentVos.size();
		for (int i = 0; i < size; i++) {
			ContentVo contentVo = contentVos.get(i);
			title.add(contentVo.getTitle());
		}
		return title;
	}

	@Override
	public PageInfo<ContentVo> getArticlesWithpage(ContentVoExample commentVoExample, Integer page, Integer limit) {
		PageHelper.startPage(page, limit);
		List<ContentVo> contentVos = contentDao.selectByExampleWithBLOBs(commentVoExample);
		return new PageInfo<>(contentVos);
	}

	@Override
	@Transactional
	public String deleteByCid(Integer cid) {
		ContentVo contents = this.getContents(cid + "");
		if (null != contents) {
			contentDao.deleteByPrimaryKey(cid);
			relationshipService.deleteById(cid, null);
			return WebConst.SUCCESS_RESULT;
		}
		return "数据为空";
	}

	/**
	 * 根据标题查询文章
	 * 
	 * @param String
	 *            title
	 * @return ContentVo
	 */
	public List<ContentVo> getCont(String title) {
		return contentDao.getCont(title);
	}

	@Override
	public void updateCategory(String ordinal, String newCatefory) {
		ContentVo contentVo = new ContentVo();
		contentVo.setCategories(newCatefory);
		ContentVoExample example = new ContentVoExample();
		example.createCriteria().andCategoriesEqualTo(ordinal);
		contentDao.updateByExampleSelective(contentVo, example);
	}

	@Override
	@Transactional
	public String updateArticle(ContentVo contents) {
		if (null == contents) {
			return "文章对象为空";
		}
		if (StringUtils.isBlank(contents.getTitle())) {
			return "文章标题不能为空";
		}
		if (StringUtils.isBlank(contents.getContent())) {
			return "文章内容不能为空";
		}
		int titleLength = contents.getTitle().length();
		if (titleLength > WebConst.MAX_TITLE_COUNT) {
			return "文章标题过长";
		}
		int contentLength = contents.getContent().length();
		if (contentLength > WebConst.MAX_TEXT_COUNT) {
			return "文章内容过长";
		}
		if (null == contents.getAuthorId()) {
			return "请登录后发布文章";
		}
		if (StringUtils.isBlank(contents.getSlug())) {
			contents.setSlug(null);
		}
		int time = DateKit.getCurrentUnixTime();
		contents.setModified(time);
		Integer cid = contents.getCid();
		contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

		contentDao.updateByPrimaryKeySelective(contents);
		relationshipService.deleteById(cid, null);
		metasService.saveMetas(cid, contents.getTags(), Types.TAG.getType());
		metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY.getType());

		return WebConst.SUCCESS_RESULT;
	}

	@Override
	/**
	 * 文章的点击数hits加1
	 * 
	 * @param Integer
	 *            cid
	 * @return int 影响行数
	 */
	public int hitsAddsByOne(Integer cid) {
		int hits = contentDao.hitsAddsByOne(cid);
		return hits;
	}

	/**
	 * 更新文章的最近阅读时间
	 */
	@Override
	public int updateArticleReadtime(Integer cid) {
		return contentDao.updateArticleReadtime(cid);
	}

	/**
	 * 查询最近添加的20篇文章
	 * 
	 * @param String
	 *            title
	 * @return List
	 */
	public List<ContentVo> getNewlyArticles(Integer num) {
		return contentDao.getNewlyArticles(num);
	}
}
