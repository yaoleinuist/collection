package com.lzhsite.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

/**
 * Spark Core提供了三种创建RDD的方式，包括：使用程序中的集合创建RDD；使用本地文件创建RDD；使用HDFS文件创建RDD。
 * 
 * 使用HDFS文件创建RDD
 * 案例：统计文本文件字数
 * @author Administrator
 *
 */
public class HDFSFile {
	
	public static void main(String[] args) {
		// 创建SparkConf
		// 修改：去除setMaster()设置，修改setAppName()
		SparkConf conf = new SparkConf()
				.setAppName("HDFSFile"); 
		// 创建JavaSparkContext
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		// 使用SparkContext以及其子类的textFile()方法，针对HDFS文件创建RDD
		// 只要把textFile()内的路径修改为hdfs文件路径即可
		
		// spark默认会为hdfs文件的每一个block创建一个partition，但是也可以通过textFile()的第二个参数手动设置分区数量，只能比block数量多，不能比block数量少。
		
		//Spark的textFile()除了可以针对上述几种普通的文件创建RDD之外，还有一些特列的方法来创建RDD：
		//1、SparkContext.wholeTextFiles()方法，可以针对一个目录中的大量小文件，返回<filename, fileContent>组成的pair，作为一个PairRDD，而不是普通的RDD。普通的textFile()返回的RDD中，每个元素就是文件中的一行文本。
		//2、SparkContext.sequenceFile[K, V]()方法，可以针对SequenceFile创建RDD，K和V泛型类型就是SequenceFile的key和value的类型。K和V要求必须是Hadoop的序列化类型，比如IntWritable、Text等。
		//3、SparkContext.hadoopRDD()方法，对于Hadoop的自定义输入类型，可以创建RDD。该方法接收JobConf、InputFormatClass、Key和Value的Class。
		//4、SparkContext.objectFile()方法，可以针对之前调用RDD.saveAsObjectFile()创建的对象序列化的文件，反序列化文件中的数据，并创建一个RDD。
		JavaRDD<String> lines = sc.textFile("hdfs://hadoop.senior02:8020/user/beifeng/mapreduce/wordcount/input/wc.input", 1);
		
		// 统计文本文件内的字数
		JavaRDD<Integer> lineLength = lines.map(new Function<String, Integer>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Integer call(String v1) throws Exception {
				return v1.length();
			}
			
		});
		
		int count = lineLength.reduce(new Function2<Integer, Integer, Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1 + v2;
			}
			
		});
		
		System.out.println("文件总字数是：" + count);  
		
		// 关闭JavaSparkContext
		sc.close();
	}
	
}
