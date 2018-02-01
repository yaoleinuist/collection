package com.lzhsite.spark;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * 取最大的前3个数字
 * @author Administrator
 *
 */
public class Top3 {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("Top3")
				.setMaster("local");  
		JavaSparkContext sc = new JavaSparkContext(conf);
	
		JavaRDD<String> lines = sc.textFile("file:///D:/大数据/spark/Spark深入剖析/第一章：Spark核心编程/资料/第40讲-Spark核心编程：高级编程之topntop.txt");
		
		//转化为map的RDD是为了调用sortByKey
		JavaPairRDD<Integer, String> pairs = lines.mapToPair(
				
				new PairFunction<String, Integer, String>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Tuple2<Integer, String> call(String t) throws Exception {
						return new Tuple2<Integer, String>(Integer.valueOf(t), t);
					}
					
				});
		
		//降序排
		JavaPairRDD<Integer, String> sortedPairs = pairs.sortByKey(false);
		//转回单个元素的RDD
		JavaRDD<Integer> sortedNumbers = sortedPairs.map(
				
				
				//第一个参数是入参，第二个是返回值
				new Function<Tuple2<Integer,String>, Integer>() {

					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(Tuple2<Integer, String> v1) throws Exception {
						return v1._1;
					}
					
				});
		
		List<Integer> sortedNumberList = sortedNumbers.take(3);
		
		for(Integer num : sortedNumberList) {
			System.out.println(num);
		}
		
		sc.close();
	}
	
}
