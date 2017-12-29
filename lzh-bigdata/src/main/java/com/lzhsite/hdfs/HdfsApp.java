package com.lzhsite.hdfs;

import java.io.File;
import java.io.FileInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
/**
 * http://blog.csdn.net/woaidapaopao/article/details/52006688
 * @author lzhcode
 *
 */

public class HdfsApp {

	public static FileSystem getFileSystem() throws Exception {

		// creat configuration , default & site.xml
		Configuration configuration = new Configuration();
	    //configuration.set("fs.defaultFS","hdfs://ns1");

		// get filesystem
		FileSystem fileSystem = FileSystem.get(configuration);

		return fileSystem;
	}

	public static void read(String fileName) throws Exception {

		// String fileName = "/user/beifeng/temp/conf/core-site.xml";

		FileSystem fileSystem = getFileSystem();

		// read Path
		Path readPath = new Path(fileName);

		// input Stream
		FSDataInputStream inStream = fileSystem.open(readPath);

		try {
			IOUtils.copyBytes(inStream, System.out, 4096, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(inStream);
		}

	}

	public static void main(String[] args) throws Exception {

		// read file
		// String fileName = "/user/beifeng/temp/conf/core-site.xml";
		// read(fileName);

		String fileName = "/user/beifeng/wordcount/input/WordCountMapReduce.txt";

		FileSystem fileSystem = getFileSystem();

		// write Path
		Path writePath = new Path(fileName);

		// get input Stream
		FileInputStream inStream = new FileInputStream(new File(
				"D://WordCountMapReduce.txt"));

		// write file
		FSDataOutputStream outStream = fileSystem.create(writePath);
		try {
			IOUtils.copyBytes(inStream, outStream, 4096, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(inStream);
			IOUtils.closeStream(outStream);
		}

	}

}
