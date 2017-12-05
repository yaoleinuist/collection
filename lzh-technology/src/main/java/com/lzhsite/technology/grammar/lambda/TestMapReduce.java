package com.lzhsite.technology.grammar.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lzhsite.entity.Averager;
import com.lzhsite.entity.User;

public class TestMapReduce {
	 private static List<User> users = Arrays.asList( 
	            new User(1, "张三", 12,1), 
	            new User(2, "李四", 21, 2), 
	            new User(3,"王五", 32, 1), 
	            new User(4, "赵六", 32,2)); 
	  
	    public static void main(String[] args) { 
	        reduceAvg(); 
	        reduceSum(); 
	  
	          
	        //与stream.reduce方法不同，Stream.collect修改现存的值，而不是每处理一个元素，创建一个新值 
	        //获取所有男性用户的平均级别
	        Averager averageCollect = users.parallelStream() 
	                .filter(p -> p.getSex() == 1) 
	                .map(User::getScale) 
	                .collect(Averager::new, Averager::accept, Averager::combine); 
	  
	        System.out.println("Average age of male members: " 
	                + averageCollect.average()); 
	  
	        //获取年龄大于12的用户列表 
	        List<User> list = users.parallelStream().filter(p -> p.getScale() > 12) 
	                .collect(Collectors.toList()); 
	        System.out.println(list); 
	  
	        //按性别统计用户数 
	        Map<Integer, Integer> map = users.parallelStream().collect( 
	                Collectors.groupingBy(User::getSex, 
	                        Collectors.summingInt(p -> 1))); 
	        System.out.println(map); 
	  
	        //按性别获取用户名称 
	        Map<Integer, List<String>> map2 = users.stream() 
	                .collect( 
	                        Collectors.groupingBy( 
	                                User::getSex, 
	                                Collectors.mapping(User::getName, 
	                                        Collectors.toList()))); 
	        System.out.println(map2); 
	          
	        //按性别求年龄的总和 
	        Map<Integer, Integer> map3 = users.stream().collect( 
	                Collectors.groupingBy(User::getSex, 
	                        Collectors.reducing(0, User::getScale, Integer::sum))); 
	  
	        System.out.println(map3); 
	          
	        //按性别求年龄的平均值 
	        Map<Integer, Double> map4 = users.stream().collect( 
	                Collectors.groupingBy(User::getSex, 
	                        Collectors.averagingInt(User::getScale))); 
	        System.out.println(map4); 
	  
	    } 
	  
	    // 注意，reduce操作每处理一个元素总是创建一个新值， 
	    // Stream.reduce适用于返回单个结果值的情况 
	    //获取所有用户的平均级别
	    private static void reduceAvg() { 
	        // mapToInt的pipeline后面可以是average,max,min,count,sum 
	        double avg = users.parallelStream().mapToInt(User::getScale) 
	                .average().getAsDouble(); 
	  
	        System.out.println("reduceAvg User Age: " + avg); 
	    } 
	  
	    //获取所有用户的年龄总和 
	    private static void reduceSum() { 
	        double sum = users.parallelStream().mapToInt(User::getScale) 
	                .reduce(0, (x, y) -> x + y); // 可以简写为.sum() 
	  
	        System.out.println("reduceSum User Age: " + sum); 
	    } 
}
