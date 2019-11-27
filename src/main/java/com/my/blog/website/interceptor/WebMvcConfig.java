package com.my.blog.website.interceptor;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 向mvc中添加自定义组件 Created by BlueT on 2017/3/9.
 * 用户访问控制
 */
@Component
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Resource
	private BaseInterceptor baseInterceptor;

	/**
	 * 注册拦截器，不拦截静态资源
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(baseInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/admin/css/**","/admin/editor/**","/admin/images/**","/admin/js/**"
					,"/admin/plugins/**","/dependents/**","/user/css/**","/user/img/**","/user/js/**","/attached/**");
	}

	/**
	 * 添加静态资源文件，外部可以直接访问地址
	 * 注意：springboot2.0之后会拦截静态资源，用此方法拦截静态资源会无效
	 * @param registry
	 */
	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:" + TaleUtils.getUploadFilePath() + "upload/");
		super.addResourceHandlers(registry);
	}*/
	
	
}
