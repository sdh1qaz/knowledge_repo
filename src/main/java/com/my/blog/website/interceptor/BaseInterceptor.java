package com.my.blog.website.interceptor;

import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IOptionService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.utils.*;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.admin.ArticleController;
import com.my.blog.website.dto.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
	private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
	private static final String USER_AGENT = "user-agent";

	@Resource
	private IUserService userService;

	@Resource
	private IOptionService optionService;

	private MapCache cache = MapCache.single();

	@Resource
	private Commons commons;

	@Resource
	private AdminCommons adminCommons;

	// 此方法在处理请求前被调用
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		
		// 获取应用上下文
		String contextPath = request.getContextPath();
		// 获取请求的uri
		String uri = request.getRequestURI();

		LOGGE.info("UserAgent: {}", request.getHeader(USER_AGENT));
		LOGGE.info("用户访问地址: {}, 来路地址: {}", uri, IPKit.getIpAddrByRequest(request));

		// 请求拦截处理
		// 从session中取出当前登录的账户
		UserVo user = TaleUtils.getLoginUser(request);
		if (null == user) {
			Integer uid = TaleUtils.getCookieUid(request);
			if (null != uid) {
				// 这里还是有安全隐患,cookie是可以伪造的
				user = userService.queryUserById(uid);
				request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
			}
		}else {
			LOGGE.info("当前登录的用户：" + user.getUsername());
		}
		
		//获取当前用户类型
		String userType = getAdminOrUser(user);
		
		//如果uri以/admin开头，且不以/admin/login开头，且当前没有登录的用户或为前台用户，
		//则转向后台登录页面
		if (uri.startsWith(contextPath + "/admin") && !uri.startsWith(contextPath + "/admin/login") 
				&& ("0".equals(userType) || "1".equals(userType))) {
			response.sendRedirect(request.getContextPath() + "/admin/login");
			return false;
		}
		//如果uri不以/admin开头，即前台请求，且不以/user/login开头，且当前没有登录的用户或为后台用户，
		//则转向前台登录页面
		if(!uri.startsWith(contextPath + "/admin") && !uri.startsWith(contextPath + "/user/login")
				&& ("0".equals(userType) || "2".equals(userType)) && !uri.contains("preview")) {
			response.sendRedirect(request.getContextPath() + "/user/login");
			return false;
		}
		
		// 设置get请求的token
		if (request.getMethod().equals("GET")) {
			String csrf_token = UUID.UU64();
			// 默认存储30分钟
			cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, uri, 30 * 60);
			request.setAttribute("_csrf_token", csrf_token);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
		OptionVo ov = optionService.getOptionByName("site_record");
		httpServletRequest.setAttribute("commons", commons);// 一些工具类和公共方法
		httpServletRequest.setAttribute("option", ov);
		httpServletRequest.setAttribute("adminCommons", adminCommons);
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {

	}
	
	//判断当前用户类型，用户为空0，前台用户为,1，后台用户为2
	private String getAdminOrUser(UserVo user) {
		if (null == user) {
			return "0";
		}else if ("dhsu".equals(user.getUsername())) {
			return "1";
		}else {
			return "2";
		}
	}
}
