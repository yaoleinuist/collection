package com.lzhsite.core.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	/**
     * @Description: 通过文件读取文件内容(txt、properties)
     * @param file
     * @return
     */
    public static String readContentByFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = bfr.readLine()) != null) {
                result.append(lineTxt).append("\n");
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * @Description: 通过文件路径读取文件内容(txt、properties)
     * @param filePath
     * @return
     */
    public static String readContentByPath(String filePath) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = bfr.readLine()) != null) {
                result.append(lineTxt).append("\n");
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 通过URL获取文本内容
     * @param urlStr
     * @return
     */
    public static String readContentByUrl(String urlStr) throws Exception {
        String res=null;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            res = new String(readInputStream(inputStream),"utf-8");

        return res;
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
	

    /**
     * @Description: 读取文件内容(txt、properties)
     * @param filePath
     * @return
     */
    public static String readFileFromPath(String filePath) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = bfr.readLine()) != null) {
                result.append(lineTxt).append("\n");
            }
            bfr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    /**
     * 加载classpath下的lua脚本
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String loadScript(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            buffer.append(line).append(System.lineSeparator());
        }
        return buffer.toString();
    }
}
