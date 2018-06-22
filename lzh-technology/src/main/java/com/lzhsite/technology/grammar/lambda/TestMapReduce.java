package com.lzhsite.technology.grammar.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.lzhsite.dto.AveragerDto;
import com.lzhsite.entity.User;

public class TestMapReduce {
	private static List<User> users = Arrays.asList(new User(1, "张三", 12, 1), new User(2, "李四", 21, 2),
			new User(3, "王五", 32, 1), new User(4, "赵六", 32, 2));

	public static void main(String[] args) {
		reduceAvg();
		reduceSum();

		// 与stream.reduce方法不同，Stream.collect修改现存的值，而不是每处理一个元素，创建一个新值
		// 获取所有男性用户的平均级别
		AveragerDto averageCollect = users.parallelStream().filter(p -> p.getSex() == 1).map(User::getScale)
				.collect(AveragerDto::new, AveragerDto::accept, AveragerDto::combine);

		System.out.println("Average age of male members: " + averageCollect.average());

		// 获取级别大于12的用户列表
		List<User> list = users.parallelStream().filter(p -> p.getScale() > 12).collect(Collectors.toList());
		System.out.println(list);

		// 按性别统计用户数
		Map<Integer, Integer> map = users.parallelStream()
				.collect(Collectors.groupingBy(User::getSex, Collectors.summingInt(p -> 1)));
		System.out.println(map);

		// 按性别获取用户名称
		Map<Integer, List<String>> map2 = users.stream()
				.collect(Collectors.groupingBy(User::getSex, Collectors.mapping(User::getName, Collectors.toList())));
		System.out.println(map2);

		// 按性别求年龄的总和
		Map<Integer, Integer> map3 = users.stream()
				.collect(Collectors.groupingBy(User::getSex, Collectors.reducing(0, User::getScale, Integer::sum)));

		System.out.println(map3);

		// 按性别求年龄的平均值
		Map<Integer, Double> map4 = users.stream()
				.collect(Collectors.groupingBy(User::getSex, Collectors.averagingInt(User::getScale)));
		System.out.println(map4);

	}

	// 注意，reduce操作每处理一个元素总是创建一个新值，
	// Stream.reduce适用于返回单个结果值的情况
	// 获取所有用户的平均级别
	private static void reduceAvg() {
		// mapToInt的pipeline后面可以是average,max,min,count,sum
		double avg = users.parallelStream().mapToInt(User::getScale).average().getAsDouble();

		System.out.println("reduceAvg User Age: " + avg);
	}

	// 获取所有用户的年龄总和
	private static void reduceSum() {
		double sum = users.parallelStream().mapToInt(User::getScale).reduce(0, (x, y) -> x + y); // 可以简写为.sum()

		System.out.println("reduceSum User Age: " + sum);
	}

	/**
	 * 使用java的lambda表达式实现word count的两种方法
	 */
	@Test
	public void test1() {
		// 创建数据源
		List<String> list = new ArrayList<>();
		list.add("Hello world");
		list.add("Hello java");
		list.add("This is a java program");
		list.add("Give your program a little Spring");
		list.add("So You Think You Can Dance");
		list.add("Word Count");
		list.add("Hello Job");
		list.add("To be or not to be is a question");
		// 方法一将单词放入一个hashmap中
		// 结果Map，用于存放Word和Count
		Map<String, Integer> map = new HashMap<>();

		// Lambda表达式
		list.stream()
				// flatMap方法可以将一个元素映射为一个流，然后整合，此处将一句话映射为一个word流
				.flatMap(line -> Arrays.stream(line.toLowerCase().split(" ")))
				// 将各单词放入HashMap中
				.forEach(word -> {
					if (map.containsKey(word)) {
						int count = map.get(word) + 1;
						map.put(word, count);
					} else {
						map.put(word, 1);
					}
				});

		// 输出结果
		map.entrySet().forEach(System.out::println);
		//////////////////////////////////////////////////////////////////////////
		// 方法二： 先对单词流进行排序，然后reduce进行计数
		// 临时变量，用于计数(这里用数组是因为lambda表达式内不能改变外部变量，java的闭包有缺陷)
		int[] count = { 1 };
		// Lambda表达式
		list.stream()
				// flatMap方法可以将一个元素映射为一个流，然后整合，此处将一句话映射为一个word流
				.flatMap(line -> Arrays.stream(line.toLowerCase().split(" ")))
				// 将单词排序
				.sorted().reduce("", (preWord, word) -> {
					if (word.equals(preWord)) {
						count[0]++;
					} else if (!"".equals(preWord)) {
						System.out.println(preWord + " = " + count[0]);
						count[0] = 1;
					}
					return word;
				});
	}
}
