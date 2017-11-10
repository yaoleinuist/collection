package com.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/TestStream")
public class TestStreamController {

	
	@RequestMapping(value="/test1")
	public void  name() {

        
	     try {
				 URL url = new URL("http://localhost/uploadFile/replaceFile?id=203");
		        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		        // 发送POST请求必须设置如下两行  
		
		        conn.setDoOutput(true);  
		        conn.setUseCaches(false);  
		        conn.setRequestMethod("POST");  
		        conn.setRequestProperty("Content-Type","text/html");  
		        conn.setRequestProperty("Cache-Control","no-cache");  
		        conn.setRequestProperty("Charsert", "UTF-8");   
		   
					conn.connect();
		
		        conn.setConnectTimeout(10000);  
		        OutputStream out =conn.getOutputStream();  
		
		        URL u = new URL("http://localhost/uploadFile/get?id=123");
		        ByteArrayOutputStream os=new ByteArrayOutputStream();
		        BufferedImage imageOrigin = ImageIO.read(u);
		       
		        ImageIO.write(imageOrigin, "png", os); //利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
		        byte b[]=os.toByteArray();
		
		        ByteArrayInputStream in   = new ByteArrayInputStream(b); 
		
		        int bytes = 0;  
		        byte[] buffer = new byte[1024];  
		        while ((bytes = in.read(buffer)) != -1) {  
		            out.write(buffer, 0, bytes);  
		        }  
		        in.close();  
		        out.flush();  
		        out.close();   
		   
		        conn.getInputStream();  
		        conn.disconnect();
			   System.out.println("aaaa");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
