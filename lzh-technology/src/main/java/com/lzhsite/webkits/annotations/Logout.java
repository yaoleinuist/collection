package com.lzhsite.webkits.annotations;

import java.lang.annotation.*;

/**
 * 标记为登出的OpenAPI接口.
 * 配置了AuthorizationInterceptor拦截器的才能生效,加了此注解后会将登录凭证token从redis中删除.
 *
 * Created by liuliling on 17/6/28.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logout {
}
