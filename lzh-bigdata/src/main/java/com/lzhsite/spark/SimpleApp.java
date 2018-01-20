package com.lzhsite.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class SimpleApp {
	
	
	
	public static void main(String[] args) {
	 
        String logFile = "/user/beifeng/mapreduce/wordcount/input/wc.input"; 
        SparkConf sparkConf =new SparkConf().setAppName("SimpleApp");
        sparkConf.setMaster("yarn-client");
        sparkConf.set("spark.yarn.dist.files", "file:///d:/demosrc/lzh-maven-parent/lzh-bigdata/src/main/resources/yarn-site.xml");
        sparkConf.set("spark.yarn.jar", "file:///d:/demosrc/lzh-maven-parent/lzh-bigdata/src/main/webapp/WEB-INF/lib/spark-assembly-1.6.1-hadoop2.5.0-cdh5.3.6.jar");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        sc.addJar("file:///d:/demosrc/lzh-maven-parent/lzh-bigdata/target/lzh-bigdata.jar");
        JavaRDD<String> logData = sc.textFile(logFile).cache();

        long numAs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("a"); }
        }).count();

        long numBs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("b"); }
        }).count();

        System.out.println("Lines with a: " + numAs +",lines with b: " + numBs);
    }
}
