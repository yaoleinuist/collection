package com.lzhsite.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nt on 2015-06-17.
 */
public class RegexUtils {

    private static final String ERROR_PATTERN = "^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$";

    /**
     * check is matched by input pattern
     *
     * @param str
     * @param pattern
     * @return
     */
    public static boolean isMatched(String str, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(str);
        return matcher.matches();
    }

    /**
     * check is error code
     *
     * @param errorCode
     * @return
     */
    public static boolean isErrorCode(String errorCode){
        return isMatched(errorCode,ERROR_PATTERN);
    }

}
