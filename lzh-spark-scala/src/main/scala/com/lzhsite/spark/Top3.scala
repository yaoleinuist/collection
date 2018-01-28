package com.lzhsite.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

/**
 * @author Administrator
 */
object Top3 {
  
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
        .setAppName("Top3")
        .setMaster("local")  
    val sc = new SparkContext(conf)
    
    val lines = sc.textFile("file:///D:/大数据/spark/Spark深入剖析/第一章：Spark核心编程/资料/第40讲-Spark核心编程：高级编程之topn/top.txt", 1)
    val pairs = lines.map { line => (line.toInt, line) }
    val sortedPairs = pairs.sortByKey(false)
    val sortedNumbers = sortedPairs.map(sortedPair => sortedPair._1)  
    val top3Number = sortedNumbers.take(3)
    
    for(num <- top3Number) {
      println(num)  
    }
  }
  
}