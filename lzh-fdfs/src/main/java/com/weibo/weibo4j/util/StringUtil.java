package com.weibo.weibo4j.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * @author Orion
 * @version 1.0 创建时间：Aug 16, 2016 11:15:55 AM
 * 类说明 处理字符串
 */

public class StringUtil {
	
	 private static Logger log = Logger.getLogger(StringUtil.class);
	 
	 public static String str;
	 
	 public static final String EMPTY_STRING = "";
 
	
	/**
	 * 字符串不为null 或者 ""返回true 否则返回false
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		if(null == str || "".equals(str) || str.length() == 0){
			return false;
		}else{
			return true;
		}
		
		
	}
	public static String format(String str){
		if(str.equals("是")){
			return "0";
		}else if(str.equals("否")){
			return "1";
		}
		return "";
	}
	//
	public static boolean isValidDate(String s)
    {
        try
        {    
        	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
             dateFormat.parse(s);
             return true;
         }
        catch (Exception e)
        {
            return false;
        }
    }
	
	public static boolean isEmpty (Object o) {
		return o == null || "".equals(o);
	}
	
	public static boolean isEmpty (List l) {
		return l == null || l.size() == 0;
	}
	
	public static boolean isNull (Object o) {
		return o == null || o.equals("null");
	}
	
	public static String getString (Object o) {
		String s = "";
		try {
			s = o.toString();
		} catch (Exception e) {
			
		}
		return s;
	}
	
	public static String append(String... strarr){
		StringBuffer sb = new StringBuffer();
		for(String str: strarr){
			sb.append(str);
		}
		return sb.toString();
	}
	
	
	/**
	 * 根据指定的字符分割字符串
	 * 
	 * @param str
	 *            String
	 * @param delim
	 *            String
	 * @return ArrayList
	 */
	public static ArrayList stringSplit(String str, String delim) {
		ArrayList list = new ArrayList();
		StringTokenizer strtoken = new StringTokenizer(str, delim);
		while (strtoken.hasMoreElements()) {
			list.add(strtoken.nextElement());
		}
		return list;
	}

	public static String get8859toGBKStr(String str) {
		String encodedstr = "";
		if (str != null) {
			try {
				encodedstr = new String(str.getBytes("iso-8859-1"), "GBK");
			} catch (UnsupportedEncodingException ex) {
				log.error("8859转换成GBK编码出错！", ex);
			}
		}
		return encodedstr;
	}

	public static String get8859toUTF8Str(String str) {
		String encodedstr = "";
		if (str != null) {
			try {
				encodedstr = new String(str.getBytes("iso-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				log.error("8859转换成UTF-8编码出错！", ex);
			}
		}
		return encodedstr;
	}

	public static String getGBKto8859Str(String str) {
		String encodedstr = "";
		if (str != null) {
			try {
				encodedstr = new String(str.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException ex) {
				log.error("GBK转换成8859编码出错！", ex);
			}
		}
		return encodedstr;
	}

	public static String getUTF8to8859Str(String str) {
		String encodedstr = "";
		if (str != null) {
			try {
				encodedstr = new String(str.getBytes("UTF-8"), "iso-8859-1");
			} catch (UnsupportedEncodingException ex) {
				log.error("UTF-8转换成8859编码出错！", ex);
			}
		}
		return encodedstr;
	}

	public static String getGBKtoUTF8Str(String str) {
		String encodedstr = "";
		if (str != null) {
			try {
				encodedstr = new String(str.getBytes("GBK"), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				log.error("GBK转换成UTF-8编码出错！", ex);
			}
		}
		return encodedstr;
	}

	/**
	 * 集合数据转换成以逗号分割的数据，主要用于sql语句的in条件
	 * 
	 * @param list
	 *            ArrayList
	 * @return String
	 */
	public static String list2string(ArrayList list) {
		StringBuffer sb = new StringBuffer();
		if (list != null) {
			int length = list.size();
			if (length != 0) {
				sb.append(list.get(0).toString());
				for (int i = 1; i < length; i++) {
					sb.append(",");
					sb.append(list.get(i));
				}
			}
		}
		return sb.toString();
	}
	
	
	public static String toString(Object str){
		return toString(str,null);
	}
	

	public static String toString(Object str,String nullValue) {
		if (str == null) {
			return nullValue;
		}
		return str.toString();
	}

 

	/**
	 * String.split方法的特殊字符处理。
	 * 
	 * @param symbol
	 * @return
	 */
	private static Set<String> splitSymbolSet = new HashSet<String>();
	static {
		splitSymbolSet.add("$");
		splitSymbolSet.add(".");
		splitSymbolSet.add("*");
		splitSymbolSet.add("^");
		splitSymbolSet.add("?");
		splitSymbolSet.add("+");
		splitSymbolSet.add("|");
		splitSymbolSet.add("\\");
		splitSymbolSet.add("[");
		splitSymbolSet.add("]");
		splitSymbolSet.add("{");
		splitSymbolSet.add("}");
		splitSymbolSet.add("(");
		splitSymbolSet.add(")");
	}

	public static String encodeSplitSymbol(String symbol) {
		if (splitSymbolSet.contains(symbol)) {
			symbol = "\\" + symbol;
		}
		return symbol;
	}
	
	public static String unicode2String(String unicodeStr){  
	    StringBuffer sb = new StringBuffer();  
	    String str[] = unicodeStr.toUpperCase().split("U");  
	    for(int i=0;i<str.length;i++){  
	      if(str[i].equals("")) continue;  
	      char c = (char)Integer.parseInt(str[i].trim(),16);  
	      sb.append(c);  
	    }  
	    return sb.toString();  
	  }  
	
	public static String string2Unicode(String s) {  
	    try {  
	      StringBuffer out = new StringBuffer("");  
	      byte[] bytes = s.getBytes("unicode");  
	      for (int i = 2; i < bytes.length - 1; i += 2) {  
	        out.append("u");  
	        String str = Integer.toHexString(bytes[i + 1] & 0xff);  
	        for (int j = str.length(); j < 2; j++) {  
	          out.append("0");  
	        }  
	        String str1 = Integer.toHexString(bytes[i] & 0xff);  
	  
	        out.append(str);  
	        out.append(str1);  
	        out.append(" ");  
	      }  
	      return out.toString().toUpperCase();  
	    }  
	    catch (UnsupportedEncodingException e) {  
	      e.printStackTrace();  
	      return null;  
	    }  
	  }   

    public static boolean isPhone(String mobiles) {
        Pattern p = Pattern.compile("^(177|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}