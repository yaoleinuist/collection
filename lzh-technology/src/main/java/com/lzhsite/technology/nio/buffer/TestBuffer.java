package com.lzhsite.technology.nio.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

/*
 * 一、缓冲区（Buffer）：在 Java NIO 中负责数据的存取。缓冲区就是数组。用于存储不同数据类型的数据
 * 
 * 根据数据类型不同（boolean 除外），提供了相应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 
 * 上述缓冲区的管理方式几乎一致，通过 allocate() 获取缓冲区
 * 
 * 二、缓冲区存取数据的两个核心方法：
 * put() : 存入数据到缓冲区中
 * get() : 获取缓冲区中的数据
 * 
 * 三、缓冲区中的四个核心属性：
 * capacity : 容量，表示缓冲区中最大存储数据的容量。一旦声明不能改变。
 * limit : 界限，表示缓冲区中可以操作数据的大小。（limit 后数据不能进行读写）
 * position : 位置，表示缓冲区中正在操作数据的位置。
 * 
 * mark : 标记，表示记录当前 position 的位置。可以通过 reset() 恢复到 mark 的位置
 * 
 * 0 <= mark <= position <= limit <= capacity
 * 
 * 四、直接缓冲区与非直接缓冲区：
 * 非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在 JVM 的内存中
 * 直接缓冲区：通过 allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 */
public class TestBuffer {

	@Test
	public void test() {
		System.out.println("++++++++++++test begin++++++++++++");
		ByteBuffer b=ByteBuffer.allocate(15);
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		for(int i=0;i<10;i++){
			b.put((byte)i);
		}
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		b.flip();
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		for(int i=0;i<5;i++){
			System.out.print(b.get());			
		}
		System.out.println();
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		b.flip();
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		System.out.println("++++++++++++test end++++++++++++");
	}
	
	@Test
	public void test2() {
		System.out.println("++++++++++++test2 begin++++++++++++");
		ByteBuffer b=ByteBuffer.allocate(15);
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		for(int i=0;i<5;i++){
			System.out.print(b.get());			
		}
		System.out.println();
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		System.out.println("++++++++++++test2 end++++++++++++");
	}
	
	@Test
	public void testRest() {
		System.out.println("++++++++++++testRest begin++++++++++++");
		ByteBuffer b=ByteBuffer.allocate(15);
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		for(int i=0;i<5;i++){
			b.put((byte)i);			
		}
		System.out.println("limit="+b.limit()+" capacity="+b.capacity()+" position="+b.position());
		b.clear();
		for(int i=0;i<5;i++){
			System.out.print(b.get());			
		}
		System.out.println();
		System.out.println("++++++++++++testRest end++++++++++++");
	}
	
	@Test
	public void testSlice() {
		System.out.println("++++++++++++testSlice begin++++++++++++");
		ByteBuffer b=ByteBuffer.allocate(15);
		for(int i=0;i<10;i++){
			b.put((byte)i);
		}
		b.position(2);
		b.limit(6);
		ByteBuffer subBuffer=b.slice();
		for (int i = 0; i < subBuffer.capacity(); i++) { 
		     byte bb = subBuffer.get(i); 
		     bb*= 10; 
		     subBuffer.put(i, bb); 
		}
		
		b.position( 0 ); 
		b.limit(b.capacity()); 
		while(b.hasRemaining()){
			System.out.print(b.get()+" ");
		}
		System.out.println();
		System.out.println("++++++++++++testSlice end++++++++++++");
		
	}
	
	@Test
	public void testReadOnlyBuffer(){
		System.out.println("++++++++++++testReadOnlyBuffer begin++++++++++++");
		ByteBuffer b=ByteBuffer.allocate(15);
		for(int i=0;i<10;i++){
			b.put((byte)i);
		}
		ByteBuffer readOnly=b.asReadOnlyBuffer();
		readOnly.flip();  //为读写切换时做准备
		while(readOnly.hasRemaining()){
			System.out.print(readOnly.get()+" ");
		}
		System.out.println();
		
		b.put(2, (byte)20);
		//readOnly.put(2, (byte)20);
		
		readOnly.flip();
		while(readOnly.hasRemaining()){
			System.out.print(readOnly.get()+" ");
		}
		System.out.println();
		System.out.println("++++++++++++testReadOnlyBuffer end++++++++++++");
	}
	
	
	@Test
	public void testFileMap() throws IOException{
		System.out.println("++++++++++++testFileMap begin++++++++++++");
		RandomAccessFile raf = new RandomAccessFile("D:\\skio\\ybc.sql", "rw"); 
		FileChannel fc = raf.getChannel(); 
		//文件映射到内存
		MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, raf.length());
		//CharBuffer cbb=mbb
		while(mbb.hasRemaining()){
			System.out.print((char)mbb.get());
		}
		mbb.put(0,(byte)98);
		raf.close();
		System.out.println();
		System.out.println("++++++++++++testFileMap end++++++++++++");
	}
	
	@Test
	public void test3(){
		String str = "abcde";
		
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		buf.put(str.getBytes());
		
		buf.flip();
		
		byte[] dst = new byte[buf.limit()];
		buf.get(dst, 0, 2);
		System.out.println(new String(dst, 0, 2));
		System.out.println(buf.position());
		
		//mark() : 标记
		buf.mark();
		
		buf.get(dst, 2, 2);
		System.out.println(new String(dst, 2, 2));
		System.out.println(buf.position());
		
		//reset() : 恢复到 mark 的位置
		buf.reset();
		System.out.println(buf.position());
		
		//判断缓冲区中是否还有剩余数据
		if(buf.hasRemaining()){
			
			//获取缓冲区中可以操作的数量
			System.out.println(buf.remaining());
		}
	}
	
	

}
