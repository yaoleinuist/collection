package com.lzhsite.webkits.security.authentication;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OpenAPI接口身份认证接口定义.
 * Created by liuliling on 17/6/14.
 */
public interface AuthenticationService {

    /**
     * 处理登录逻辑.
     * 接口描述:    根据XContent.getCurrentContext()可获取上下文数据;
     *             验证用户名/密码或调用第三方处理登录请求;
     *             登录成功后,生成令牌token用于维护用户的登录态,拼接tokenPrefix(可用于设置redis键值分组)作为key缓存到redis中,value为XSession会话信息,token有效期可通过tokenExpire()方法设置,默认:2h);
     *             处理过程中可操作xContent.getSession()将登录后的用户信息等数据存放在xSession中(同一会话中获取的xSession信息相同)
     *             每次请求处理完毕拦截器会将该xsession更新到redis中,请求进来时会从redis中读取并设置到XContent.xSession对象中.
     *
     * @return 返回是否登录成功
     */
    boolean doLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 处理身份验证逻辑.
     * 接口说明:    该接口有默认的实现:将请求头中的token值取出,通过在redis中查找是否存在token来判断身份是否有效;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来保存想要保存的会话数据。
     *
     * @return 返回是否验证成功
     */
    boolean doAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 处理登出逻辑.
     * 接口描述:    该接口有默认的实现:将请求头中的token取出,将其对应在redis中的数据删除掉;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来获取当前会话信息
     *
     * @return 返回是否登出成功
     */
    boolean doLogout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 初始化session对象
     */
    void initSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 请求处理结束,视图render之前
     */
    void complete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView);

}
