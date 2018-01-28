package com.lzhsite.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

/**
 * @author Administrator
 */
object WordCount {
  
  def main(args: Array[String]) {
    val conf = new SparkConf()
        .setAppName("WordCount");
    val sc = new SparkContext(conf)
  
    val lines = sc.textFile("hdfs://hadoop.senior02:8020/user/beifeng/mapreduce/wordcount/input/wc.input", 1); 
    val words = lines.flatMap { line => line.split(" ") }   
    val pairs = words.map { word => (word, 1) }   
    val wordCounts = pairs.reduceByKey { _ + _ }
    
    wordCounts.foreach(wordCount => println(wordCount._1 + " appeared " + wordCount._2 + " times."))  
  }
  
}