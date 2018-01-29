package com.lzhsite.spark;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

/**
 * 广播变量
 * @author Administrator
 * 
 * Spark提供的Broadcast Variable，是只读的。并且在每个节点上只会有一份副本，
 * 而不会为每个task都拷贝一份副本。因此其最大作用，就是减少变量到各个节点的网络传输消耗，以及在各个节点上的内存消耗。
 * 此外，spark自己内部也使用了高效的广播算法来减少网络消耗。
 * 可以通过调用SparkContext的broadcast()方法，来针对某个变量创建广播变量。然后在算子的函数内，使用到广播变量时，
 * 每个节点只会拷贝一份副本了。每个节点可以使用广播变量的value()方法获取值。记住，广播变量，是只读的。
 *
 */
public class BroadcastVariable {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf()
				.setAppName("BroadcastVariable") 
				.setMaster("local"); 
		JavaSparkContext sc = new JavaSparkContext(conf);
	
		// 在java中，创建共享变量，就是调用SparkContext的broadcast()方法
		// 获取的返回结果是Broadcast<T>类型
		final int factor = 3;
		final Broadcast<Integer> factorBroadcast = sc.broadcast(factor);
		
		List<Integer> numberList = Arrays.asList(1, 2, 3, 4, 5);
		
		JavaRDD<Integer> numbers = sc.parallelize(numberList);
		
		// 让集合中的每个数字，都乘以外部定义的那个factor
		JavaRDD<Integer> multipleNumbers = numbers.map(new Function<Integer, Integer>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Integer call(Integer v1) throws Exception {
				// 使用共享变量时，调用其value()方法，即可获取其内部封装的值
				int factor = factorBroadcast.value();
				return v1 * factor;
			}
			
		});
		
		multipleNumbers.foreach(new VoidFunction<Integer>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void call(Integer t) throws Exception {
				System.out.println(t);  
			}
			
		});
		
		sc.close();
	}
	
}
