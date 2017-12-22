package com.lzhsite.core.utils.cryptors;

import java.security.MessageDigest;

/**
 * SHA1加密算法.
 * Created by liuliling on 17/6/16.
 */
public class SHA1Cryptor {

    private SHA1Cryptor() {
    }

    private static SHA1Cryptor instance = new SHA1Cryptor();

    public String encrypt(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            throw new RuntimeException("SHA1加密失败", e);
        }
    }

    public static SHA1Cryptor getInstance() {
        return instance;
    }

}
