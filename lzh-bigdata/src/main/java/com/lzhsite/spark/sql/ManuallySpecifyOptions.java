package com.lzhsite.spark.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * 手动指定数据源类型
 * @author Administrator
 *
 */
public class ManuallySpecifyOptions {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf()   
				.setAppName("ManuallySpecifyOptions");
		JavaSparkContext sc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(sc);
		
		DataFrame peopleDF = sqlContext.read().format("json")
				.load("hdfs://hadoop.senior02:9000/TestDir/people.json");
		peopleDF.select("name").write().format("parquet")  
				.save("hdfs://hadoop.senior02:9000/TestDir/peopleName_java");     
	}
	
}
