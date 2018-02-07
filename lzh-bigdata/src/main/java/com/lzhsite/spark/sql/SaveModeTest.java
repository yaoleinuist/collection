package com.lzhsite.spark.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;

/**
 * SaveModel示例
 * @author Administrator
 *
 */
public class SaveModeTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		SparkConf conf = new SparkConf()   
				.setAppName("SaveModeTest");
		JavaSparkContext sc = new JavaSparkContext(conf);
		SQLContext sqlContext = new SQLContext(sc);
		
		DataFrame peopleDF = sqlContext.read().format("json")
				.load("hdfs://hadoop.senior02:9000/TestDir/people.json"); 
		peopleDF.save("hdfs://hadoop.senior02:9000/TestDir/people_savemode_test", "json", SaveMode.Append);
	}
	
}
