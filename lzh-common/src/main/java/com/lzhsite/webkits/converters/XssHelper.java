package com.lzhsite.webkits.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzhsite.core.utils.CollectionUtils;
import com.lzhsite.core.utils.StringUtils;
import com.lzhsite.ensure.Ensure;
 
public class XssHelper {
	 private static List<String> blackList;

	    private static List<String> htmlWhiteList;

	    static {
	        //xss注入点黑名单过滤
	        blackList = new ArrayList<>();
	        blackList.add("&NewLine;");
	        blackList.add("&colon;");
	        blackList.add("<script>");
	        blackList.add("<iframe ");
	        blackList.add("<meta ");
	        blackList.add("javascript:");
	        blackList.add("vbscript:");
	        blackList.add("eval");
	        blackList.add("expression");
	        blackList.add("alert");
	        blackList.add("formaction");
	        blackList.add("<embed ");
	        //js事件过滤
	        blackList.add("onabort");
	        blackList.add("onblur");
	        blackList.add("onchange");
	        blackList.add("onclick");
	        blackList.add("ondblclick");
	        blackList.add("onerror");
	        blackList.add("onfocus");
	        blackList.add("onkeydown");
	        blackList.add("onkeypress");
	        blackList.add("onkeyup");
	        blackList.add("onload");
	        blackList.add("onmousedown");
	        blackList.add("onmousemove");
	        blackList.add("onmouseout");
	        blackList.add("onmouseover");
	        blackList.add("onmouseup");
	        blackList.add("onreset");
	        blackList.add("onresize");
	        blackList.add("onselect");
	        blackList.add("onsubmit");
	        blackList.add("onunload");

	        htmlWhiteList = new ArrayList<>();
	        htmlWhiteList.add("&#39;");
	    }

	    /**
	     * 清除富文本字段的xss
	     *
	     * @param keys
	     * @param jsonBody
	     * @return
	     */
	    private static Object cleanRichTextXss(List<String> keys, Object jsonBody) {
	        if (jsonBody == null) {
	            return null;
	        }
	        if (jsonBody instanceof String) {
	            String s1 = (String)jsonBody;
	            cleanNormalXss(s1);
	            cleanHtmlXss(s1);
	            return s1;
	        }
	        if (jsonBody instanceof JSONObject) {
	            if (CollectionUtils.isEmpty(keys)) {
	                return jsonBody;
	            }
	            Set<Map.Entry<String, Object>> set = ((JSONObject) jsonBody).entrySet();
	            JSONObject cleanObj = new JSONObject();
	            for (Map.Entry<String, Object> obj : set) {
	                if (needPutToJson(keys, obj.getKey())) {
	                    List<String> next = getMultistageKey(keys, obj.getKey());
	                    cleanObj.put(obj.getKey(), cleanRichTextXss(next, obj.getValue()));
	                } else {
	                    cleanObj.put(obj.getKey(), obj.getValue());
	                }
	            }
	            return cleanObj;
	        }
	        return jsonBody;
	    }

	    /**
	     * 检测是否需要放入json字符串中，多个key前缀一样肯能放入重复
	     *
	     * @param keys
	     * @param key
	     * @return
	     */
	    private static boolean needPutToJson(List<String> keys, String key) {
	        for (String k : keys) {
	            if (k.startsWith(key)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    /**
	     * 清除html编码的xss攻击风险
	     *
	     * @param s
	     */
	    private static void cleanHtmlXss(String s) {
	        if (StringUtils.isEmpty(s)) {
	            return;
	        }
	        for (String whiteStr : htmlWhiteList) {
	            s = s.replaceAll(whiteStr, "");
	        }
	        Pattern pattern = Pattern.compile("&#(.*?);", Pattern.CASE_INSENSITIVE);
	        Ensure.that(pattern.matcher(s).find()).isFalse("F_WEBKITS_COMMON_1011");
	    }

	    /**
	     * 处理普通文本的xss攻击风险
	     *
	     * @param s
	     */
	    private static void cleanNormalXss(String s) {
	        s = s.replaceAll("\0", "");
	        for (String str : blackList) {
	            Ensure.that(s.contains(str)).isFalse("F_WEBKITS_COMMON_1011");
	        }
	    }

	    /**
	     * 跳过指定的字段，清除剩余的xss
	     *
	     * @param jsonBody
	     * @param richTextField
	     * @return
	     */
	    public static String cleanXssForRichTextField(String jsonBody, String richTextField) {
	        if (jsonBody == null) {
	            return null;
	        }
	        if (richTextField == null) {
	            return jsonBody;
	        }
	        Map<String, Object> map = (Map) JSONObject.parse(jsonBody);
	        List checkList = Arrays.asList(richTextField.split(","));
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
	            String key = entry.getKey();
	            if (entry.getValue() == null) {
	                continue;
	            } else {
	                List<String> richTextKey;
	                if (checkList.contains(key)) {
	                    richTextKey = Arrays.asList(key);
	                } else {
	                    richTextKey = getMultistageKey(checkList, key);
	                }
	                if (CollectionUtils.isNotEmpty(richTextKey)) {
	                    map.put(key, cleanRichTextXss(richTextKey, entry.getValue()));
	                } else {
	                    map.put(key, entry.getValue());
	                }
	            }
	        }
	        return JSON.toJSONString(map);
	    }

	    /**
	     * 检测是否包含多级属性校验
	     *
	     * @param checkList
	     * @param key
	     * @return
	     */
	    private static List<String> getMultistageKey(List<String> checkList, String key) {
	        if (CollectionUtils.isEmpty(checkList)) {
	            return null;
	        }
	        List<String> containList = new ArrayList<>();
	        for (String val : checkList) {
	            if (val.startsWith(key + ".")) {
	                containList.add(getNextXssProperties(val));
	            }
	        }
	        return containList;
	    }

	    /**
	     * 富文本包含多级属性的时候，获取下个属性名
	     *
	     * @param val
	     * @return
	     */
	    private static String getNextXssProperties(String val) {
	        int i = val.indexOf(".");
	        if (i == -1 || i + 1 == val.length()) {
	            return null;
	        }
	        return val.substring(i + 1, val.length());
	    }

}
