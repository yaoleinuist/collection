package com.weibo.weibo4j.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Encoder;

public class SignUtil {
	public static String getSign(TreeMap<String, String> treemap, String cilentId) {

		StringBuffer A = new StringBuffer();
		Iterator titer = treemap.entrySet().iterator();
		while (titer.hasNext()) {
			Map.Entry ent = (Map.Entry) titer.next();
			
		 
			String key = ent.getKey()==null?  null:ent.getKey().toString();
			String value = ent.getValue()==null?  null:ent.getValue().toString();

			if (StringUtils.isEmpty(value)) {
				continue;
			} else {
				// System.out.println(key + "*" + value);
				A.append(key + "=" + value + "&");
			}

		}
		if (A.length() > 0) {
			A = A.deleteCharAt(A.length() - 1);
			A.append(cilentId);
			System.out.println("B=" + A);
		}

		String mysign = null;
		mysign = URLEncoder.encode(getMd5(A.toString()));
		System.out.println(mysign);

		return mysign;
	}

	public static String getMd5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}
}
