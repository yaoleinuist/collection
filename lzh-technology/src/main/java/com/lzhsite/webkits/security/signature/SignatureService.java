package com.lzhsite.webkits.security.signature;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OpenAPI接口数据安全认证接口定义.
 * Created by liuliling on 17/6/14.
 */
public interface SignatureService {


    /**
     * 签名验证
     */
    boolean doVerifySign(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /**
     * 接口返回时签名加密
     */
    void doWriteSign(HttpServletResponse httpServletResponse);

}
