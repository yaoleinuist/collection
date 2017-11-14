package com.lzhsite.technology.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import com.lzhsite.technology.redis.sessionframework.GlobalConstant;
import com.lzhsite.util.CookieUtil;
import com.lzhsite.util.StringUtil;
/**
 * 实现详见com.lzhsite.technology.redis.sessionframework
 * 分布式session
 * @author lzhcode
 *
 */
public class SessionFilter extends OncePerRequestFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(SessionFilter.class);
	@Override
	protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 HttpServletRequest httpServletRequest =(HttpServletRequest) request;
		 HttpServletResponse httpServletResponse =(HttpServletResponse) response;
		 
		// 从cookie中获取sessionId，如果此次请求没有sessionId，重写为这次请求设置一个sessionId
		String sid = CookieUtil.getCookieValue(httpServletRequest, GlobalConstant.JSESSIONID);
		if (StringUtils.isEmpty(sid) || sid.length() != 36) {
			sid = StringUtil.getUuid();
			CookieUtil.setCookie(httpServletRequest,httpServletResponse, GlobalConstant.JSESSIONID, sid, 60 * 60);
		}

		// 交给自定义的HttpServletRequestWrapper处理
		filterChain.doFilter(new HttpServletRequestWrapper(httpServletRequest), httpServletResponse);
	}
 
 

}
