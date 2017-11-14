package com.lzhsite.technology.pic;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lzhsite.util.HttpUtils;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class HtmlToImage {
	 
	/**
	* 填充图片为png格式，填充部分为透明色
	* @param srcImage 源文件
	* @param descFile 目标路径
	* @param suffix_src 文件类型
	* @param destWidth  设置图片宽度
	* @param destHight  设置图片高度
	* @return
	*/
	public static boolean reduceImage_scale(final BufferedImage srcImage,
	File descFile, String suffix_src, int destWidth, int destHight) {
		BufferedImage outImage = null;
		try {
			outImage = new BufferedImage(destWidth, destHight,
			BufferedImage.TYPE_INT_RGB);// 2.jpg
			Graphics2D graphics2D = outImage.createGraphics();
			outImage = graphics2D.getDeviceConfiguration().createCompatibleImage(destWidth, destHight, Transparency.TRANSLUCENT);
			graphics2D.dispose();
			graphics2D = outImage.createGraphics();
			int oldheight = srcImage.getHeight();
			int oldwidth = srcImage.getWidth();
			// 设置图片居中显示
			graphics2D.drawImage(srcImage, (destWidth - oldwidth) / 2,
			(destHight - oldheight) / 2, null);
			ImageIO.write(outImage, suffix_src, descFile);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	    public static void main(String[] args) {
	    	
	        try {
	        	
		    	HttpClient client =  new DefaultHttpClient();  
	            
		    	String html = HttpUtils.getHtml(client,"http://localhost/btkjsite_designcloud/before/kindeditorContent?designid=28");
	            //1图片的大小会比原网页设计偏大
		    	//2输出格式必须为png否则背景颜色会有偏差
	            String outFileImagePath = "D:/TestReentrantReadWriteLock.png";
	           
	            HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
	            imageGenerator.loadHtml(html);
	            imageGenerator.saveAsImage(outFileImagePath);
	            
                
	            
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
