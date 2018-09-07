package com.lzhsite.core.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class FileUtil extends FileUtils{

	/**
	 * MultipartFile 转换成File
	 *
	 * @param multfile
	 *            原文件类型
	 * @return File
	 * @throws IOException
	 */
	private static File multipartToFile(MultipartFile multfile) throws IOException {
		CommonsMultipartFile cf = (CommonsMultipartFile) multfile;

        File file = null;
        if(multfile.equals("")||multfile.getSize()<=0){
            multfile = null;
        }else{
            InputStream ins = multfile.getInputStream();
            file=new File(multfile.getOriginalFilename());
            inputStreamToFile(ins, file);
        }
		return file;

	}

	public static File inputStreamToFile(InputStream ins,File file) {
		  try {
		   OutputStream os = new FileOutputStream(file);
		   int bytesRead = 0;
		   byte[] buffer = new byte[8192];
		   while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
		    os.write(buffer, 0, bytesRead);
		   }
		   os.close();
		   ins.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		  return  file;
	}

	/**
	 * 根据url拿取file
	 * 
	 * @param url
	 * @param suffix
	 *            文件后缀名
	 */
	public static File createFileByUrl(String url, String suffix) {
		byte[] byteFile = getImageFromNetByUrl(url);
		if (byteFile != null) {
			File file = getFileFromBytes(byteFile, suffix);
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 根据地址获得数据的字节流
	 * 
	 * @param strUrl
	 *            网络连接地址
	 * @return
	 */
	public static byte[] getImageFromNetByUrl(String strUrl) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			byte[] btImg = readInputStream(inStream);// 得到图片的二进制数据
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	// 创建临时文件
	private static File getFileFromBytes(byte[] b, String suffix) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = File.createTempFile("pattern", "." + suffix);
			System.out.println("临时文件位置：" + file.getCanonicalPath());
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	
	/**
	 * Java 中不创建临时文件的情况下如何删除文件前面指定行内容呢？
	 * 答：这个问题看起来可能会觉得奇怪，也会觉得好像很容易似的，很多人的做法可能会选择使用第三方工具类或者创建 tmp 
	 * 文件从指定行开始复制写入，完事删除原文件且对 tmp 文件进行重命名。而如果要求不允许创建 tmp 文件如何操作呢？
	 * 下面给出一个实现方式，其核心就是利用 RandomAccessFile 实现，
	 * 原理就是把后面内容依次读出来覆盖前面的内容，这样就不用新建文件了 
	 * 这个问题的的应用场景也很多，譬如我们想往一个文件记录一些信息，当文件大小大于指定阈值时就让文件缩小一半
	 * （即丢弃前面的记录，保留最近追加的），就可以用上面的工具类实现。
	 * @param file
	 * @param clearHeaderLins
	 * @return
	 * @throws IOException
	 */
	public boolean removeFileHeaderLines(File file, int clearHeaderLins) throws IOException {
		
			    RandomAccessFile accessFile = null;
			    try{
			        accessFile = new RandomAccessFile(file, "rw");

			        long writePosition = accessFile.getFilePointer();
			        for (int i = 0 ; i < clearHeaderLins; i++){

			            String line = accessFile.readLine();
			            if(line == null){
			                break;
			            }
			        }
			        long readPosition = accessFile.getFilePointer();
			        byte[] buffer = new byte[1024];
			        int num;

			        while (-1 != (num = accessFile.read(buffer))) {
			        	//设置文件的写入位置
			            accessFile.seek(writePosition);
			            accessFile.write(buffer, 0, num);
			            readPosition += num;
			            writePosition += num;
			          //设置文件的读取位置
			            accessFile.seek(readPosition);
			        }
			        accessFile.setLength(writePosition);
			        return true;

			    } catch(Throwable e){
			        return false;
			    } finally{
			      IOUtils.closeQuietly(accessFile);
			    }
	 }

}
