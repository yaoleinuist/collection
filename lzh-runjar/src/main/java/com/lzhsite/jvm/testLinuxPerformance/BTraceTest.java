package com.lzhsite.jvm.testLinuxPerformance;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BTraceTest {
	public static void writeFile(String filename) throws IOException, InterruptedException{
		File f=new File(filename);
		if(!f.exists()){
			f.createNewFile();
		}
		DataOutputStream fos=new DataOutputStream(new FileOutputStream(f));
		fos.writeBytes(filename);
		fos.close();
		Thread.sleep(200);
	}
	
	public static String readFile(String filename) throws IOException, InterruptedException{
		File f=new File(filename);
		DataInputStream fis=new DataInputStream (new FileInputStream(f));
		String re=fis.readLine();
		fis.close();
		f.delete();
		Thread.sleep(200);
		return re;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		while(true){
			String filename=Integer.toString(((int)(Math.random()*100)));
			writeFile(filename);
			readFile(filename);
		}
	}
}
