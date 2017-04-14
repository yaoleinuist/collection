package com.technology.nio.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;
 
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
	
	
}
