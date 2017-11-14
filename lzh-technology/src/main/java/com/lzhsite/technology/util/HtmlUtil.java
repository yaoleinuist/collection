package com.lzhsite.technology.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlUtil {
	 /**
     * 拿到html
     *
     * @return string
     */
    public static String findHtmlStringByFilePath(String HTML_PATH) throws IOException {
        File file = new File(HTML_PATH);
        InputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();
        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            result.append(temp);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return new String(result.toString().getBytes(),"UTF-8");
    }

    public static String findHtmlStringByURL(String HTML_URL) throws IOException {
 
    	URL url = new URL(HTML_URL);
 
        Document document=Jsoup.parse(url,5 * 1000);
        return document.html();
      
    }
   
}
