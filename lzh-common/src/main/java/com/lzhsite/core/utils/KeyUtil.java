package com.lzhsite.core.utils;

/**
 * redis key组装
 * Created by pangpeijie on 16/8/19.
 */
public class KeyUtil {
    public static String generteKeyWithPlaceholder(String keyModule,Object... key){
        return String.format(keyModule, key);
    }

    public final static void main(String[] args){
//        System.out.println(generteKeyWithPlaceholder(RedisConstant.ACCOUNT_ROLE_ALL_PARENT_KEY,"asdads"));
    }
}
