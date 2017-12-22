package com.lzhsite.webkits.security.signature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OpenAPI接口数据签名认证.
 * Created by liuliling on 17/6/14.
 */
public abstract class AbstractSignatureService implements SignatureService {

    protected abstract boolean verifySign(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    protected abstract void writeSign(HttpServletResponse httpServletResponse);

    @Override
    public boolean doVerifySign(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return verifySign(httpServletRequest, httpServletResponse);
    }

    @Override
    public void doWriteSign(HttpServletResponse httpServletResponse){
        writeSign(httpServletResponse);
    }


}
