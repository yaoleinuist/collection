/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhsite.spark;

import scala.Tuple2;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class JavaWordCountCluster {
	
  private static final Pattern SPACE = Pattern.compile(" ");
 
  
  public static void main(String[] args) throws Exception {
 
	// 如果要在spark集群上运行，需要修改的，只有两个地方
	// 第一，将SparkConf的setMaster()方法给删掉，默认它自己会去连接
	// 第二，我们针对的不是本地文件了，修改为hadoop hdfs上的真正的存储大数据的文件
	// 实际执行步骤：
	// 1、将spark.txt文件上传到hdfs上去
	// 2、使用我们最早在pom.xml里配置的maven插件，对spark工程进行打包
	// 3、将打包后的spark工程jar包，上传到机器上执行
	// 4、编写spark-submit脚本
	// 5、执行spark-submit脚本，提交spark应用到集群执行
    SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount");
    /**
     *    local 本地单线程
	 *    local[K] 本地多线程（指定K个内核）
	 *    local[*] 本地多线程（指定所有可用内核）
	 *    spark://HOST:PORT 连接到指定的 Spark standalone cluster master，需要指定端口。
	 *    mesos://HOST:PORT 连接到指定的 Mesos 集群，需要指定端口。
	 *    yarn-client客户端模式 连接到 YARN 集群。需要配置 HADOOP_CONF_DIR。
	 *    yarn-cluster集群模式 连接到 YARN 集群。需要配置 HADOOP_CONF_DIR
     */
    sparkConf.setMaster("spark://hadoop.senior02:7077");
    //sparkConf.setMaster("local[*]");
    
    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
  
    //如果要在本地运行Spark standalone cluster,这个属性必须制定(工程打包后放置在windows上的路径)
    //ctx.addJar("file:///d:/demosrc/lzh-maven-parent/lzh-bigdata/target/lzh-bigdata.jar");
    
    //JavaRDD<String> lines = ctx.textFile("hdfs://hadoop.senior02:8020/user/beifeng/mapreduce/wordcount/input/wc.input", 1);
    JavaRDD<String> lines = ctx.textFile("/user/beifeng/mapreduce/wordcount/input/wc.input", 1);
    
    
    
    JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
      @Override
      public Iterable<String> call(String s) {
        return Arrays.asList(SPACE.split(s));
      }
    });

    JavaPairRDD<String, Integer> ones = words.mapToPair(new PairFunction<String, String, Integer>() {
      @Override
      public Tuple2<String, Integer> call(String s) {
        return new Tuple2<String, Integer>(s, 1);
      }
    });

    JavaPairRDD<String, Integer> counts = ones.reduceByKey(new Function2<Integer, Integer, Integer>() {
      @Override
      public Integer call(Integer i1, Integer i2) {
        return i1 + i2;
      }
    });

    List<Tuple2<String, Integer>> output = counts.collect();
    for (Tuple2<?,?> tuple : output) {
      System.out.println(tuple._1() + ": " + tuple._2());
    }
    ctx.stop();
  }
}
