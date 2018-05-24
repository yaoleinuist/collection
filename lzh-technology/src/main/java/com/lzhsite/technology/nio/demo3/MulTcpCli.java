package com.lzhsite.technology.nio.demo3;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MulTcpCli {
	public static void main(String[] args) throws Exception {
		//long start = System.currentTimeMillis();
		mul_thread_pool_deal();
		//long end= System.currentTimeMillis();
		//System.out.println(end-start);
		//mul_thread_deal();
	}
	public static void mul_thread_deal(){
		ArrayList<Thread> thread_l = new ArrayList<Thread>();
		for (int i=0;i<10;i++){
			thread_l.add(new Thread(new MulTcpCliImpl()));
		}
		for (Thread thread:thread_l){
			thread.start();
		}
		
	}
	public static void mul_thread_pool_deal(){
		ArrayList<Thread> thread_l = new ArrayList<Thread>();
		for (int i=0;i<10;i++){
			thread_l.add(new Thread(new MulTcpCliImpl()));
		}
		for (Thread thread:thread_l){
			thread.start();
		}
		
	}
	
	public static void mul_thread_deal2(){
		
		new Thread(new MulTcpCliImpl()).start();
		new Thread(new MulTcpCliImpl()).start();
		new Thread(new MulTcpCliImpl()).start();
	}
}

class MulTcpCliImpl implements Runnable {
	Socket socket = null;

	public MulTcpCliImpl() {
	}

	public void run() {
		try {
			System.out.println(Thread.currentThread().getName() + " start running ... aaaa");
			//test();
			Socket client = null;
	        DataOutputStream out = null;
	        DataInputStream in = null;
	        //System.out.println(Thread.currentThread().getName() );
	        try {
	            client = new Socket("127.0.0.1", 5100);
	            client.setSoTimeout(30000);
	            out = new DataOutputStream( (client.getOutputStream()));

	            String query = "GB   ";
	            query=query+Thread.currentThread().getName();
	            byte[] request = query.getBytes();
	            out.write(request);
	            out.flush();
	            client.shutdownOutput();

	            in = new DataInputStream(client.getInputStream());
	            byte[] reply = new byte[400];
	            in.read(reply);
	            String buf=new String(reply, "GBK");
	            buf=Thread.currentThread().getName()+"   "+buf;
	            //System.out.println("Time: " + new String(reply, "GBK")+" | "+Thread.currentThread().getName() );
	            System.out.println(buf);
	            in.close();
	            out.close();
	            client.close();
	        }
	        catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test() {
        Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            client = new Socket("127.0.0.1", 5100);
            client.setSoTimeout(30000);
            out = new DataOutputStream( (client.getOutputStream()));

            String query = "GB";
            query=query+Thread.currentThread().getName();
            byte[] request = query.getBytes();
            out.write(request);
            out.flush();
            client.shutdownOutput();

            in = new DataInputStream(client.getInputStream());
            byte[] reply = new byte[400];
            in.read(reply);
            String buf=new String(reply, "GBK");
            buf=buf+" | "+Thread.currentThread().getName();
            //System.out.println("Time: " + new String(reply, "GBK")+" | "+Thread.currentThread().getName() );
            System.out.println(buf);

            in.close();
            out.close();
            client.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
	
	
}
