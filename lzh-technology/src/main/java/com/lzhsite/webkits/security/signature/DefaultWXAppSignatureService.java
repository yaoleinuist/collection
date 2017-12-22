package com.lzhsite.webkits.security.signature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.cryptors.SHA1Cryptor;
import com.lzhsite.webkits.context.XContext;

/**
 * Created by ruancl@xkeshi.com on 2017/6/15.
 */
public class DefaultWXAppSignatureService extends AbstractSignatureService {

    @Override
    public boolean verifySign(HttpServletRequest request, HttpServletResponse response) {
        XContext context = XContext.getCurrentContext();
        Object session_key = context.getSession().getAttribute("session_key");
        String rawData = context.getParameter("rawData");
        String signature = context.getParameter("signature");

        Ensure.that(session_key).isNotNull("F_WEBKITS_SECURITY_1007");
        Ensure.that(rawData).isNotNull("F_WEBKITS_SECURITY_1008");
        Ensure.that(signature).isNotNull("F_WEBKITS_SECURITY_1009");
        Ensure.that(signature.equals(SHA1Cryptor.getInstance().encrypt(rawData + session_key)))
                .isTrue("F_WEBKITS_SIGN_1002");
        return true;
    }

    @Override
    public void writeSign(HttpServletResponse httpServletResponse) {
        throw new UnsupportedOperationException("wxapp不支持此操作");
    }
}
