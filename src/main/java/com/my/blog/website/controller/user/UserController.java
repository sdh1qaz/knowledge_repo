package com.my.blog.website.controller.user;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.ErrorCode;
import com.my.blog.website.dto.MetaDto;
import com.my.blog.website.dto.Types;
import com.my.blog.website.model.Bo.ArchiveBo;
import com.my.blog.website.model.Bo.CommentBo;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ItemVo;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.ICommentService;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.IItemVoService;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.service.dbupdate.UpdateService;
import com.my.blog.website.service.impl.HistoryQueue;
import com.my.blog.website.utils.CMDUtil;
import com.my.blog.website.utils.IPKit;
import com.my.blog.website.utils.PatternKit;
import com.my.blog.website.utils.TaleUtils;
import com.vdurmont.emoji.EmojiParser;

import net.sf.json.JSONArray;

@Controller
public class UserController extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Resource
	private IContentService contentService;

	@Resource
	private ICommentService commentService;

	@Resource
	private IMetaService metaService;

	@Resource
	private ISiteService siteService;

	@Resource
	private IItemVoService itemVoService;

	@Resource
	private HistoryQueue<ContentVo> histQ;

	@Resource
	private UpdateService updateService;

	// private ExecutorService executorService = Executors.newCachedThreadPool();

	/**
	 * 判断队列中是否已有这个文章
	 * 
	 * @param contentVo
	 * @return
	 */
	public boolean isHasCont(ContentVo contentVo) {
		boolean is = false;
		// 遍历hisQ
		for (ContentVo cont : histQ) {
			if (contentVo.getTitle().equals(cont.getTitle())) {
				is = true;
			}
		}
		return is;
	}

	/**
	 * 用户登录小书包
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/user/login/check")
	public String loginCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取前台传来的用户名
		String userName = request.getParameter("userName");
		// 获取前台传来的密码
		String pwd = request.getParameter("pwd");
		UserVo userVo = new UserVo();
		userVo.setUsername(userName);
		userVo.setPassword(pwd);
		// 用户名dhsu，密码admin
		if ("dhsu".equals(userName) && "e10adc3949ba59abbe56e057f20f883e".equals(pwd)) {
			// 用户放入session
			request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY_USER, userVo);
			response.sendRedirect("/forum");
			return null;
		}
		LOGGER.info("前台登录小书包的用户名或密码错误。。。");
		// 返回登录页面
		return this.render("login");
	}
	
	/**
	 * 转向登录页面
	 */
	@RequestMapping("/user/login")
	public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		LOGGER.info("转向前台登录页面...");
		// 返回登录页面
		return this.render("login");
	}

	/**
	 * 用户登出小书包
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/user/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().removeAttribute(WebConst.LOGIN_SESSION_KEY_USER);
		LOGGER.info("前台用户退出。。。");
		// 返回登录页面
		return this.render("login");
	}

	/**
	 * 首页
	 *
	 * @return
	 */
	@GetMapping(value = "/forum")
	public String index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
		return this.index(request, 1, limit);
	}

	/**
	 * 数据库更新
	 * 
	 * @return json数组[文章名，文章cid}
	 */
	@RequestMapping("/user/updateDB")
	public @ResponseBody String updateDB(HttpServletRequest request) {
		/*
		 * executorService.submit(new Runnable() {
		 * 
		 * @Override public void run() { UpdateService.updateLocal(); } });
		 */
		updateService.updateLocal();
		return "ok";
	}

	/**
	 * 数据库同步
	 * 
	 * @return json数组[文章名，文章cid}
	 */
	@RequestMapping("/user/pushDB")
	// @ResponseBody 是向前台返回json数据，而不是视图名
	public @ResponseBody String pushDB(HttpServletRequest request) {
		/*
		 * executorService.submit(new Runnable() {
		 * 
		 * @Override public void run() { UpdateService.sendLocal(); } });
		 */
		//检测logstash进程是否存在，不存在就打开
		updateService.startLogStash();
		//发送数据库sql邮件
		updateService.sendLocal();
		return "ok";
	}

	/**
	 * 最近浏览历史的50篇文章
	 * 
	 * @return json数组[文章名，文章cid}
	 */
	@RequestMapping("/user/histQ")
	public String getHisQ(HttpServletRequest request) {
		// 使用LinkedHashMap来保持插入顺序
		// Map<String,String> hs = new LinkedHashMap<String,String>();
		List<ContentVo> articles = new ArrayList<ContentVo>();
		Queue<ContentVo> queue = histQ.getQueue();
		for (ContentVo contentVo : queue) {
			articles.add(contentVo);
		}
		Collections.reverse(articles);// 顺序倒置
		request.setAttribute("articles", articles);
		return this.render("history");
	}

	/**
	 * 最近添加的20篇文章
	 * @return json数组[文章名，文章cid}
	 */
	@RequestMapping("/user/newly")
	public String getNewlyArticles(HttpServletRequest request) {
		// 获取最近添加的20篇文章
		List<ContentVo> articles = contentService.getNewlyArticles(20);
		request.setAttribute("articles", articles);
		return this.render("newlyArticles");
	}

	/**
	 * 根据题目关键词搜索文章
	 * @param request
	 * @param p第几页
	 * @param limit每页大小
	 * @return 搜索结果主页
	 */
	@RequestMapping("user/search")
	public String listSearchResut(HttpServletRequest request,
			@RequestParam(value = "limit", defaultValue = "12") int limit, String keyword) {
		PageInfo<ContentVo> articles = contentService.getArticles(keyword, 1, limit);
		// 如果搜索的结果只有一篇文章，直接打开
		if (articles.getSize() == 1) {
			// 获取文章cid
			ContentVo cont = contentService.getCont(keyword).get(0);
			if (!isHasCont(cont)) {
				histQ.offer(cont);
			}
			if (null == cont || "draft".equals(cont.getStatus())) {
				return this.render_404();
			}
			request.setAttribute("article", cont);
			request.setAttribute("is_post", true);
			completeArticle(request, cont);
			contentService.hitsAddsByOne(Integer.valueOf(cont.getCid()));
			// 记录日志，哪个ip点击了哪篇文章
			String clientIp = IPKit.getIpAddrByRequest(request);// 客户端ip
			int port = IPKit.getPort(request);
			String article = cont.getTitle();// 文章标题
			LOGGER.info(clientIp + ":" + port + "点击了《" + article + "》");
			return this.render("post");
		}
		request.setAttribute("articles", articles);
		request.setAttribute("key", "user/search/" + keyword);// 把keyword传递给search_list.html页面便于查找下一页
		return this.render("search_result");
	}
	
	/**
	 * 搜索框自动提示
	 * @param String keyword
	 * @return arry
	 */
	@RequestMapping("user/titles")
	@ResponseBody
	public Object getSearchResults(String keyword) {
		List<String> list = contentService.getTitles(keyword);
		JSONArray arry = JSONArray.fromObject(list);
		return arry.toString();
	}

	/**
	 * 待办按钮响应
	 * 
	 * @return 待办页面
	 */
	@RequestMapping("user/listItems")
	public String listToDo(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
		PageInfo<ItemVo> pageInfo = itemVoService.getItems(1, limit);
		request.setAttribute("pageInfo", pageInfo);
		return this.render("items");
	}

	/**
	 * 已办按钮响应
	 * 
	 * @return 待办页面
	 */
	@RequestMapping("user/listDoneItems")
	public String listDone(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
		PageInfo<ItemVo> pageInfo = itemVoService.getDoneItems(1, limit);
		request.setAttribute("pageInfo", pageInfo);
		return this.render("items_done");
	}

	@GetMapping(value = "user/search/{keyword}/{page}")
	public String listPageSearchResut(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
		PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
		request.setAttribute("articles", articles);
		request.setAttribute("key", "user/search/" + keyword);// 把keyword传递给search_list.html页面便于查找下一页
		return this.render("search_result");
	}

	/**
	 * 后台文章管理根据题目关键词搜索文章
	 * @param request
	 * @param p第几页
	 * @param limit每页大小
	 * @return 搜索结果主页
	 */
	@RequestMapping("admin/search")
	public String listSearchResutAdmin(HttpServletRequest request,
			@RequestParam(value = "limit", defaultValue = "12") int limit, String keyword) {
		PageInfo<ContentVo> articles = contentService.getArticles(keyword, 1, limit);
		request.setAttribute("articles", articles);
		request.setAttribute("key", "admin/search/" + keyword);// 把keyword传递给search_list.html页面便于查找下一页
		return "admin/search_result";
	}

	/**
	 * 后台文章管理根据题目关键词搜索文章
	 * @param request
	 * @param p第几页
	 * @param limit每页大小
	 * @return 搜索结果主页
	 */
	@GetMapping(value = "admin/search/{keyword}/{page}")
	public String listPageAdminSearchResut(HttpServletRequest request, @PathVariable String keyword,
			@PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
		page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
		PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
		request.setAttribute("articles", articles);
		request.setAttribute("key", "admin/search/" + keyword);// 把keyword传递给search_list.html页面便于查找下一页
		return "admin/search_result";
	}

	/**
	 * 分页
	 *
	 * @param request
	 * @param p第几页
	 * @param limit每页大小
	 * @return 主页
	 */
	@GetMapping(value = "page/{p}")
	public String index(HttpServletRequest request, @PathVariable int p,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		p = p < 0 || p > WebConst.MAX_PAGE ? 1 : p;
		PageInfo<ContentVo> articles = contentService.getContents(p, limit);
		request.setAttribute("articles", articles);
		if (p > 1) {
			this.title(request, "第" + p + "页");
		}
		return this.render("index");
	}

	/**
	 * 文章页
	 * 
	 * @param request
	 * @param cid文章主键
	 * @return
	 */
	@GetMapping(value = { "article/{cid}", "article/{cid}.html" })
	public String getArticle(HttpServletRequest request, @PathVariable String cid) {
		//更新最近阅读时间
		int res = contentService.updateArticleReadtime(Integer.valueOf(cid));
		ContentVo contents = contentService.getContents(cid);
		LOGGER.info("更新最近阅读时间结果：res=" + res);
		// 当前文章进入浏览历史队列
		histQ.offer(contents);
		/*
		 * if (!isHasCont(contents)) { }
		 */
		if (null == contents || "draft".equals(contents.getStatus())) {
			return this.render_404();
		}
		request.setAttribute("article", contents);
		request.setAttribute("is_post", true);
		completeArticle(request, contents);
		/*
		 * if (!checkHitsFrequency(request, cid)) { updateArticleHit(contents.getCid(),
		 * contents.getHits()); }
		 */
		//点击数加1
		contentService.hitsAddsByOne(Integer.valueOf(cid));
		
		// 记录日志，哪个ip点击了哪篇文章
		String clientIp = IPKit.getIpAddrByRequest(request);// 客户端ip
		int port = IPKit.getPort(request);
		String article = contents.getTitle();// 文章标题
		LOGGER.info(clientIp + ":" + port + "点击了《" + article + "》");
		return this.render("post");

	}

	/**
	 * 文章页(预览)
	 * @param request请求
	 * @param cid文章主键
	 * @return
	 */
	@GetMapping(value = { "/article/{cid}/preview", "/article/{cid}.html" })
	public String articlePreview(HttpServletRequest request, @PathVariable String cid) {
		ContentVo contents = contentService.getContents(cid);
		if (null == contents) {
			return this.render_404();
		}
		request.setAttribute("article", contents);
		request.setAttribute("is_post", true);
		completeArticle(request, contents);
		if (!checkHitsFrequency(request, cid)) {
			updateArticleHit(contents.getCid(), contents.getHits());
		}
		return this.render("post");

	}

	/**
	 * 抽取公共方法
	 * @param request
	 * @param contents
	 */
	private void completeArticle(HttpServletRequest request, ContentVo contents) {
		if (contents.getAllowComment()) {
			String cp = request.getParameter("cp");
			if (StringUtils.isBlank(cp)) {
				cp = "1";
			}
			request.setAttribute("cp", cp);
			PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp),
					6);
			request.setAttribute("comments", commentsPaginator);
		}
	}

	/**
	 * 注销
	 * @param session
	 * @param response
	 */
	@RequestMapping("logout")
	public void logout(HttpSession session, HttpServletResponse response) {
		TaleUtils.logout(session, response);
	}

	/**
	 * 评论操作
	 */
	@PostMapping(value = "comment")
	@ResponseBody
	public RestResponseBo comment(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer cid,
			@RequestParam Integer coid, @RequestParam String author, @RequestParam String mail,
			@RequestParam String url, @RequestParam String text, @RequestParam String _csrf_token) {

		String ref = request.getHeader("Referer");
		if (StringUtils.isBlank(ref) || StringUtils.isBlank(_csrf_token)) {
			return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
		}

		String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);
		if (StringUtils.isBlank(token)) {
			return RestResponseBo.fail(ErrorCode.BAD_REQUEST);
		}

		if (null == cid || StringUtils.isBlank(text)) {
			return RestResponseBo.fail("请输入完整后评论");
		}

		if (StringUtils.isNotBlank(author) && author.length() > 50) {
			return RestResponseBo.fail("姓名过长");
		}

		if (StringUtils.isNotBlank(mail) && !TaleUtils.isEmail(mail)) {
			return RestResponseBo.fail("请输入正确的邮箱格式");
		}

		if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
			return RestResponseBo.fail("请输入正确的URL格式");
		}

		if (text.length() > 200) {
			return RestResponseBo.fail("请输入200个字符以内的评论");
		}

		String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
		Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
		if (null != count && count > 0) {
			return RestResponseBo.fail("您发表评论太快了，请过会再试");
		}

		author = TaleUtils.cleanXSS(author);
		text = TaleUtils.cleanXSS(text);

		author = EmojiParser.parseToAliases(author);
		text = EmojiParser.parseToAliases(text);

		CommentVo comments = new CommentVo();
		comments.setAuthor(author);
		comments.setCid(cid);
		comments.setIp(request.getRemoteAddr());
		comments.setUrl(url);
		comments.setContent(text);
		comments.setMail(mail);
		comments.setParent(coid);
		try {
			String result = commentService.insertComment(comments);
			cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
			cookie("tale_remember_mail", URLEncoder.encode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);
			if (StringUtils.isNotBlank(url)) {
				cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
			}
			// 设置对每个文章1分钟可以评论一次
			cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
			if (!WebConst.SUCCESS_RESULT.equals(result)) {
				return RestResponseBo.fail(result);
			}
			return RestResponseBo.ok();
		} catch (Exception e) {
			String msg = "评论发布失败";
			LOGGER.error(msg, e);
			return RestResponseBo.fail(msg);
		}
	}

	/**
	 * 分类页
	 * @return
	 */
	@GetMapping(value = "category/{keyword}")
	public String categories(HttpServletRequest request, @PathVariable String keyword,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		return this.categories(request, keyword, 1, limit);
	}

	@GetMapping(value = "category/{keyword}/{page}")
	public String categories(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
		MetaDto metaDto = metaService.getMeta(Types.CATEGORY.getType(), keyword);
		if (null == metaDto) {
			return this.render_404();
		}

		PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);

		request.setAttribute("articles", contentsPaginator);
		request.setAttribute("meta", metaDto);
		request.setAttribute("type", "分类");
		request.setAttribute("keyword", keyword);

		return this.render("page-category");
	}

	/**
	 * 归档页
	 * @return
	 */
	@GetMapping(value = "archives")
	public String archives(HttpServletRequest request) {
		List<ArchiveBo> archives = siteService.getArchives();
		request.setAttribute("archives", archives);
		return this.render("archives");
	}

	/**
	 * 友链页
	 * @return
	 */
	@GetMapping(value = "links")
	public String links(HttpServletRequest request) {
		List<MetaVo> links = metaService.getMetas(Types.LINK.getType());
		request.setAttribute("links", links);
		return this.render("links");
	}

	/**
	 * 自定义页面,如关于的页面
	 */
	@GetMapping(value = "/{pagename}")
	public String page(@PathVariable String pagename, HttpServletRequest request) {
		ContentVo contents = contentService.getContents(pagename);
		if (null == contents) {
			return this.render_404();
		}
		if (contents.getAllowComment()) {
			String cp = request.getParameter("cp");
			if (StringUtils.isBlank(cp)) {
				cp = "1";
			}
			PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp),
					6);
			request.setAttribute("comments", commentsPaginator);
		}
		request.setAttribute("article", contents);
		if (!checkHitsFrequency(request, String.valueOf(contents.getCid()))) {
			updateArticleHit(contents.getCid(), contents.getHits());
		}
		return this.render("page");
	}

	/**
	 * 搜索页
	 *
	 * @param keyword
	 * @return
	 */
	@GetMapping(value = "search/{keyword}")
	public String search(HttpServletRequest request, @PathVariable String keyword,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		return this.search(request, keyword, 1, limit);
	}

	@GetMapping(value = "search/{keyword}/{page}")
	public String search(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
		PageInfo<ContentVo> articles = contentService.getArticles(keyword, page, limit);
		request.setAttribute("articles", articles);
		request.setAttribute("type", "搜索");
		request.setAttribute("keyword", keyword);
		return this.render("page-category");
	}

	/**
	 * 更新文章的点击率
	 *
	 * @param cid
	 * @param chits
	 */
	private void updateArticleHit(Integer cid, Integer chits) {
		Integer hits = cache.hget("article" + cid, "hits");
		if (chits == null) {
			chits = 0;
		}
		hits = null == hits ? 1 : hits + 1;
		if (hits >= WebConst.HIT_EXCEED) {
			ContentVo temp = new ContentVo();
			temp.setCid(cid);
			temp.setHits(chits + hits);
			contentService.updateContentByCid(temp);
			cache.hset("article" + cid, "hits", 1);
		} else {
			cache.hset("article" + cid, "hits", hits);
		}
	}

	/**
	 * 标签页
	 *
	 * @param name
	 * @return
	 */
	@GetMapping(value = "tag/{name}")
	public String tags(HttpServletRequest request, @PathVariable String name,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {
		return this.tags(request, name, 1, limit);
	}

	/**
	 * 标签分页
	 *
	 * @param request
	 * @param name
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping(value = "tag/{name}/{page}")
	public String tags(HttpServletRequest request, @PathVariable String name, @PathVariable int page,
			@RequestParam(value = "limit", defaultValue = "12") int limit) {

		page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
		// 对于空格的特殊处理
		name = name.replaceAll("\\+", " ");
		MetaDto metaDto = metaService.getMeta(Types.TAG.getType(), name);
		if (null == metaDto) {
			return this.render_404();
		}

		PageInfo<ContentVo> contentsPaginator = contentService.getArticles(metaDto.getMid(), page, limit);
		request.setAttribute("articles", contentsPaginator);
		request.setAttribute("meta", metaDto);
		request.setAttribute("type", "标签");
		request.setAttribute("keyword", name);

		return this.render("page-category");
	}

	/**
	 * 设置cookie
	 *
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param response
	 */
	private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(false);
		response.addCookie(cookie);
	}

	/**
	 * 检查同一个ip地址是否在2小时内访问同一文章
	 *
	 * @param request
	 * @param cid
	 * @return
	 */
	private boolean checkHitsFrequency(HttpServletRequest request, String cid) {
		String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
		Integer count = cache.hget(Types.HITS_FREQUENCY.getType(), val);
		if (null != count && count > 0) {
			return true;
		}
		cache.hset(Types.HITS_FREQUENCY.getType(), val, 1, WebConst.HITS_LIMIT_TIME);
		return false;
	}

}
