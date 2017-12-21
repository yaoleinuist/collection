package com.lzhsite.webkits.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.lzhsite.annotations.Logout;
import com.lzhsite.core.context.XContext;
import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.ensure.Ensure;
import com.lzhsite.session.XSession;
import com.lzhsite.util.redis.RedisUtils;

/**
 * OpenAPI接口身份认证.
 * Created by liuliling on 17/6/14.
 */
public abstract class AbstractAuthenticationService implements AuthenticationService {

    private final static String ACCESS_TOKEN_HEADER_KEY = "x-access-token";

    /**
     * 处理登录逻辑.
     * 接口描述:    根据XContent.getCurrentContext()可获取上下文数据;
     *             验证用户名/密码或调用第三方处理登录请求;
     *             登录成功后,生成令牌token用于维护用户的登录态,拼接tokenPrefix(可用于设置redis键值分组)作为key缓存到redis中,value为XSession会话信息,token有效期可通过tokenExpire()方法设置,默认:2h);
     *             处理过程中可操作xContent.getSession()将登录后的用户信息等数据存放在xSession中(同一会话中获取的xSession信息相同);
     *             每次请求处理完毕拦截器会将该xsession更新到redis中,请求进来时会从redis中读取并设置到XContent.xSession对象中;
     *             处理完成后将x-access-token设置到response的header中,供前端nodejs获取.
     *
     * @return 返回是否登录成功
     */
    public boolean doLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 由业务组实现具体的登录逻辑处理
        String token = login(httpServletRequest, httpServletResponse);
        Ensure.that(token).isNotBlank("F_WEBKITS_SECURITY_1005");
        // 设置xsession中的sessionId和有效期
        XSession xSession = XContext.getCurrentContext().getSession();
        String xSessionId = tokenPrefix() + token;
        xSession.setSessionId(xSessionId);
        xSession.setExpire(tokenExpire());
        // 更新会话信息到redis
        RedisUtils.put(xSessionId, xSession, tokenExpire());
        // 将x-access-token设置到response的header中,供前端nodejs获取
        httpServletResponse.addHeader(ACCESS_TOKEN_HEADER_KEY, token);
        return true;
    }

    /**
     * 处理登录逻辑.
     * 接口描述:    根据XContent.getCurrentContext()可获取上下文数据;
     *             验证用户名/密码或调用第三方处理登录请求;
     *             登录成功后,生成令牌token用于维护用户的登录态,缓存到redis中,可通过tokenPrefix()设置token前缀(缓存分组),token有效期可通过tokenExpire()方法设置,默认:2h);
     *             处理过程中可操作xContent.getSession()将登录后的用户信息等数据存放在xSession中(同一个会话中,都能获取该session中的数据)。
     *
     * @return 返回是否登录成功
     */
    abstract protected String login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 处理身份验证逻辑.
     * 接口说明:    该接口有默认的实现:将请求头中的token值取出,通过在redis中查找是否存在token来判断身份是否有效;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来保存想要保存的会话数据。
     *
     * @return 返回是否验证成功
     */
    public boolean doAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // session对象的sessionId = tokenPrefix + 请求头中的x-access-token,当sessionId为空时表示请求头token为空
        XSession xSession = XContext.getCurrentContext().getSession();
        Ensure.that(xSession.getSessionId()).isNotBlank("F_WEBKITS_SECURITY_1003");
        // 身份验证
        boolean ret = authentication(httpServletRequest, httpServletResponse);
        // 如果身份验证失败,则将该会话从redis中删除,并抛出异常提示身份验证未通过
        if(!ret){
            RedisUtils.remove(xSession.getSessionId());
            throw XExceptionFactory.create("F_WEBKITS_SECURITY_1006");
        }
        return true;
    }

    /**
     * 处理身份验证逻辑.
     * 接口说明:    该接口有默认的实现:将请求头中的token值取出,通过在redis中查找是否存在token来判断身份是否有效;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来保存想要保存的会话数据。
     *
     * @return 返回是否验证成功
     */
    protected boolean authentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String xSessionId = XContext.getCurrentContext().getSession().getSessionId();
        // 验证redis中会话是否过期
        boolean exists = RedisUtils.exists(xSessionId);
        // 会话(token)已过期或失效
        Ensure.that(exists).isTrue("F_WEBKITS_SECURITY_1004");
        return true;
    }

    /**
     * 处理登出逻辑.
     * 接口描述:    该接口有默认的实现:将请求头中的token取出,将其对应在redis中的数据删除掉;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来获取当前会话信息
     *
     * @return 返回是否登出成功
     */
    public boolean doLogout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        // session对象的sessionId = tokenPrefix + 请求头中的x-access-token,当sessionId为空时表示请求头token为空
        XSession xSession = XContext.getCurrentContext().getSession();
        Ensure.that(xSession.getSessionId()).isNotBlank("F_WEBKITS_SECURITY_1003");
        // 登出处理(清除redis中的session数据)
        return logout(httpServletRequest, httpServletResponse);
    }

    /**
     * 处理登出逻辑.
     * 接口描述:    该接口有默认的实现:将请求头中的token取出,将其对应在redis中的数据删除掉;
     *             该接口可以被重写,接口实现中也可以操作XCurrent.getCurrentContext().getSession()来获取当前会话信息
     *
     * @return 返回是否登出成功
     */
    protected boolean logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String xSessionId = XContext.getCurrentContext().getSession().getSessionId();
        // 清除redis中的session数据
        RedisUtils.remove(xSessionId);
        return true;
    }

    /**
     * 初始化session对象
     */
    public void initSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        XContext xContext = XContext.getCurrentContext();
        String token = xContext.getHeader(ACCESS_TOKEN_HEADER_KEY);
        if(StringUtils.isBlank(token)) {
            return;
        }
        // 从redis中获取会话信息
        String xSessionId = tokenPrefix() + token;
        XSession xSession = RedisUtils.get(xSessionId, XSession.class);
        if(xSession == null){
            xSession = new XSession();
            xSession.setSessionId(xSessionId);
            xSession.setExpire(tokenExpire());
        }
        xContext.setSession(xSession);
    }

    /**
     * 请求处理结束,视图render之前
     */
    public void complete(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ModelAndView modelAndView){
        XContext xContext = XContext.getCurrentContext();
        // logout请求不需要更新会话信息到redis
        if(xContext.getMethodAnnotation(Logout.class) != null){
            return;
        }
        XSession xSession = xContext.getSession();
        if(xSession == null){
            return;
        }
        // 更新会话信息到redis
        String sessionId = xSession.getSessionId();
        Integer expire = xSession.getExpire();
        if(StringUtils.isNotBlank(sessionId) && expire != null && RedisUtils.exists(sessionId)){
            RedisUtils.put(sessionId, xSession, expire);
        }
    }

    /**
     * 返回token缓存在redis时的前缀,可用于分组(分组默认用冒号分隔);默认返回空字符串.
     * 接口说明:    如tokenPrefix()返回"FRAME:TOKEN:",这是将该token值缓存在redis的FRAME分组中的TOKEN组中;
     * 建议前缀格式按"项目名:键值名:"设置,如口碑项目的token前缀为"KOUBEI:TOKEN:"
     *
     * @return token前缀
     */
    protected String tokenPrefix() {
        return "";
    }

    /**
     * 返回token缓存在redis中的有效期,单位:秒;默认2小时.
     *
     * @return token有效期
     */
    protected int tokenExpire() {
        return 2 * 60 * 60;
    }

}
