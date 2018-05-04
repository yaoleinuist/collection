package com.lzhsite.core.utils;


import com.lzhsite.core.enums.PinyinEnum;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by hongping【hongping@xkeshi.com】 on 2017/6/15.
 * Description: 模块目的、功能描述
 * 中文转拼音工具类
 */
public class PinyinUtil {
    static HanyuPinyinOutputFormat format = null;


    static {
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String toPinYin(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, "", PinyinEnum.UPPERCASE);
    }

    public static String toPinYin(String str, String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinYin(str, spera, PinyinEnum.UPPERCASE);
    }


    /**
     * 方法【toPinYin】
     * 〈方法详细描述〉
     * 将str转换成拼音，如果不是汉字或者没有对应的拼音，则不作转换
     * 如： 明天 转换成 MINGTIAN
     * <PRE>
     * params [str, spera:拼接符号, type] 【备注：】
     * return java.lang.String 【备注：】
     * <PRE>
     * <p>
     * <PRE>
     * author hongping【hongping@xkeshi.com】
     * date 2017/6/15
     * <PRE>
     */
    public static String toPinYin(String str, String spera, PinyinEnum type) throws BadHanyuPinyinOutputFormatCombination {
        if (str == null || str.trim().length() == 0)
            return "";
        if (type == PinyinEnum.UPPERCASE)
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        else
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

        String py = "";
        String temp = "";
        String[] t;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((int) c <= 128)
                py += c;
            else {
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (t == null)
                    py += c;
                else {
                    temp = t[0];
                    if (type == PinyinEnum.FIRSTUPPER)
                        temp = t[0].toUpperCase().charAt(0) + temp.substring(1);
                    py += temp + (i == str.length() - 1 ? "" : spera);
                }
            }
        }
        return py.trim();
    }
}
