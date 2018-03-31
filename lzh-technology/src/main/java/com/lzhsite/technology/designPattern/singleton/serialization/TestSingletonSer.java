package com.lzhsite.technology.designPattern.singleton.serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.junit.Test;

public class TestSingletonSer {

	@Test
	public void test() throws Exception {
		SerSingleton s1 = null;
		SerSingleton s = SerSingleton.getInstance();
		//先将实例串行化到文件
		FileOutputStream fos = new FileOutputStream("SerSingleton.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(s);
		oos.flush();
		oos.close();

		//从文件读出原有的单例类
		FileInputStream fis = new FileInputStream("SerSingleton.txt");
		ObjectInputStream ois = new ObjectInputStream(fis);
		s1 = (SerSingleton) ois.readObject();
		
		Assert.assertEquals(s, s1);

	}

}
