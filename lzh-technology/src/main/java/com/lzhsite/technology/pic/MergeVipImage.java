package com.lzhsite.technology.pic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lzhsite.technology.util.HtmlImageGenerator;
import com.lzhsite.util.HttpUtils;
 

public class MergeVipImage {
	
	public static void main(String[] args){
        try {
            
          
//                    InputStream imagein=new FileInputStream("D:\\logo.jpg");
//                    InputStream imagein2=new FileInputStream("D:\\cc.jpg");
//
//                    BufferedImage image=ImageIO.read(imagein);
//                    BufferedImage image2=ImageIO.read(imagein2);
//                    Graphics g=image.getGraphics();
//                    g.drawImage(image2,image.getWidth()-image2.getWidth()-100,image.getHeight()-image2.getHeight()-100,image2.getWidth(),image2.getHeight(),null);
//                    OutputStream outImage=new FileOutputStream("D:\\systemAvatar.jpg");
//                   
//                    //jpg图片输出
//                    JPEGImageEncoder enc=JPEGCodec.createJPEGEncoder(outImage);
//                    enc.encode(image);
//                    imagein.close();
//                    imagein2.close();
//                    outImage.close();
              
        	
		            URL u = new URL("http://localhost/uploadFile/get?id=123");
		            BufferedImage imageOrigin = ImageIO.read(u);
			
			    	HttpClient client =  new DefaultHttpClient();  
		        	String html = HttpUtils.getHtml(client,"http://localhost/btkjsite_designcloud/before/kindeditorContent?designid=28");
		            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		            imageGenerator.loadHtml(html);
		            BufferedImage imageKedit =  imageGenerator.getBufferedImage();
         
	 	            Graphics g=imageOrigin.getGraphics();
	                g.drawImage(imageKedit,imageOrigin.getWidth()-imageKedit.getWidth(),imageOrigin.getHeight()-imageKedit.getHeight(),imageKedit.getWidth()-10,imageKedit.getHeight()-10,null);
	                 
	                OutputStream outImage=new FileOutputStream("D:\\test2.png");
	                
	            	//原来用 JPEGImageEncoder导出会导致背景颜色偏差,不知道为什么
	                ImageIO.write(imageOrigin, "png", outImage);
	                outImage.close();
		            
          
        } catch (Exception e) {
            e.printStackTrace();
        }
    
   }
}
