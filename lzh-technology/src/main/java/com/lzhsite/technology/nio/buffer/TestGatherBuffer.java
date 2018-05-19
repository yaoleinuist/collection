package com.lzhsite.technology.nio.buffer;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

//利用散射和聚集处理结构化数据
public class TestGatherBuffer {
	private static final String TPATH ="d:/gather.txt"; //文件路径
	static int booklen=0;
	static int authlen=0;
	@Test
	public void testGatherData() throws Exception {
		ByteBuffer bookBuf =ByteBuffer.wrap("java性能优化技巧".getBytes("utf-8"));
		ByteBuffer autBuf =ByteBuffer.wrap("葛一鸣".getBytes("utf-8"));
		booklen=bookBuf.limit();
		authlen=autBuf.limit();
		ByteBuffer[] bufs=new ByteBuffer[]{bookBuf,autBuf};
		File file =new File(TPATH);
		if(!file.exists())
			file.createNewFile();
		FileOutputStream fos =new FileOutputStream(file);
		FileChannel fc =fos.getChannel();
		fc.write(bufs);
		fos.close();
	}
	
	@Test
	public void testScatterData() throws Exception {
		ByteBuffer b1=ByteBuffer.allocate(booklen);
		ByteBuffer b2=ByteBuffer.allocate(authlen);
		ByteBuffer[] bufs =new ByteBuffer[]{b1,b2};
		File file =new File(TPATH);
		FileInputStream fis =new FileInputStream(file);
		FileChannel fc =fis.getChannel();
		fc.read(bufs);
		String bookname=new String(bufs[0].array(),"utf-8");
		String authname=new String(bufs[1].array(),"utf-8");
		System.out.println(bookname+authname);
	}
}
