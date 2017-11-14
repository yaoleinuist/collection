package com.lzhsite.technology.string;

import java.util.StringTokenizer;

import org.junit.Test;

public class StringSplitTest {

	@Test
	public void testSplit() {

		String orgStr = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			sb.append(i);
			sb.append(";");
		}
		orgStr = sb.toString();
		long begintime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			orgStr.split(";");
		}
		long endtime = System.currentTimeMillis();
		System.out.println("testSplit 花费:" + (endtime - begintime) + " ms");
	}

	@Test
	public void testToken() {

		String orgStr = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			sb.append(i);
			sb.append(";");
		}
		orgStr = sb.toString();
		long begintime = System.currentTimeMillis();
		StringTokenizer st = new StringTokenizer(orgStr, ";");
		// StringTokenizer st=orgs;
		for (int i = 0; i < 10000; i++) {
			while (st.hasMoreTokens()) {
				st.nextToken();
				// System.out.println(a);
			}
			st = new StringTokenizer(orgStr, ";");
		}
		long endtime = System.currentTimeMillis();

		System.out.println("testToken 花费:" + (endtime - begintime) + " ms");
	}

	@Test
	public void testIndexOf() {

		String orgStr = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 1000; i++) {
			sb.append(i);
			sb.append(";");
		}
		orgStr = sb.toString();

		long begintime = System.currentTimeMillis();
		String tmp = orgStr;
		for (int i = 0; i < 10000; i++) {
			while (true) {
				String splitStr = null;
				int j = tmp.indexOf(';');
				if (j < 0)
					break;
				splitStr = tmp.substring(0, j);
				tmp = tmp.substring(j + 1);
			}
			tmp = orgStr;
		}
		long endtime = System.currentTimeMillis();

		System.out.println("testIndexOf 花费:" + (endtime - begintime) + " ms");
	}
}
