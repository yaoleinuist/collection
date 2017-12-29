package com.lzhsite.hdfs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
/**
 * 向hadoop文件系统hdfs写入数据
 * @author lzhcode
 *
 */
public class FileCopyWithProgress {
	public static void main(String[] args) throws IOException {
		String localsrc = "d:/WordCountMapReduce.txt"     ;
		String dst = "hdfs://ns1/opt/datas/wc.input/test.txt";
		
		InputStream in = new BufferedInputStream(new FileInputStream(localsrc));

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);

		try (OutputStream out = fs.create(new Path(dst), new Progressable() {
			public void progress() {
				System.out.print(".");// 用于显示文件复制进度
			}
		})) {
			IOUtils.copyBytes(in, out,30000);
		 
		}
	}
}
