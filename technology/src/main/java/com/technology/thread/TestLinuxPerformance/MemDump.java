package com.technology.thread.TestLinuxPerformance;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class MemDump {
	static final int COUNT=2400;
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
		Vector<String> allFilenames=new Vector<String>();
		Vector<String> handleFilenames=new Vector<String>();
		for(int i=0;i<COUNT;i++){
			allFilenames.add(Integer.toString(i));
		}
		for(int i=0;i<COUNT;i++){
			String filename=allFilenames.get(i);
			writeFile(filename);
			readFile(filename);
			handleFilenames.add(filename);
		}
	}
}
