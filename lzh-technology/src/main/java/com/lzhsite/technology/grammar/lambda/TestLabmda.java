package com.lzhsite.technology.grammar.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

// http://tianmaotalk.iteye.com/blog/2273314
public class TestLabmda {

	/**
	 * 计算
	 */
	@Test
	public void test1() {
		// Old way:
		List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
		double total = 0;
		for (Integer cost : costBeforeTax) {
			double price = cost + 0.12 * cost;
			total = total + price;
		}
		System.out.println("Total : " + total);
		// New way:
		List<Integer> costBeforeTax2 = Arrays.asList(100, 200, 300, 400, 500);
		double bill = costBeforeTax2.stream().map((cost) -> cost + 0.12 * cost).reduce((sum, cost) -> sum + cost).get();

		System.out.println("Total : " + bill);

	}

	@Test
	public void test2() {
		// 创建一个长度大于两个字符的字符串List
		List<String> strList = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
		List<String> filtered = strList.stream().filter(x -> x.length() > 2).collect(Collectors.toList());
		System.out.printf("Original List : %s, filtered list : %s %n", strList, filtered);

		// 给List中每个元素加以一定的操作例如乘以或者除以某个值等。这些操作用map方法再好不过了
		List<String> G7 = Arrays.asList("USA", "Japan", "France", "Germany", "Italy", "U.K.", "Canada");
		String G7Countries = G7.stream().map(x -> x.toUpperCase()).collect(Collectors.joining(", "));
		System.out.println(G7Countries);

		// 利用Stream类的distinct方法过滤重复值到集合中。
		List<Integer> numbers = Arrays.asList(9, 10, 3, 4, 7, 3, 4);
		List<Integer> distinct = numbers.stream().map(i -> i * i).distinct().collect(Collectors.toList());
		System.out.printf("Original List : %s, Square Without duplicates : %s %n", numbers, distinct);
	}

	/**
	 * 排序
	 * 
	 * 第一步：去掉冗余的匿名类 Collections.sort(users,(User x, User y) ->
	 * x.getId().compareTo(y.getId()));
	 * 
	 * 第二步：使用Comparator里的comparing方法 Collections.sort(people,
	 * Comparator.comparing((User p) -> p.getId()));
	 * 
	 * 第三步：类型推导和静态导入 Collections.sort(people, comparing(p -> p.getId()));
	 * 
	 * 第四步：方法引用 Collections.sort(people, comparing(User::getId));
	 * 
	 * 第五步：使用List本身的sort更优 people.sort(comparing(Person::getId));;
	 */
	@Test
	public void test3() {
		// 普通写法：
		List<User> users = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			User user = new User();
			user.setId(i % 3);
			user.setBiFollowersCount(i % 4);
			user.setCity(RandomUtils.nextInt());
			users.add(user);
		}
		Collections.sort(users, new Comparator<User>() {
			public int compare(User x, User y) {
				// 返回结果大于0 x放后面，返回结果小于0 x放前面
				if (x.getBiFollowersCount() != 1 && y.getBiFollowersCount() != 1) {
					if (x.getId().compareTo(y.getId()) != 0) {
						// 按id降序排
						return y.getId().compareTo(x.getId());

					} else {
						// 按city升序排
						return x.getCity().compareTo(y.getCity());
					}
				} else {
					// BiFollowersCount==1时一定排在最后
					if (x.getBiFollowersCount() == 1) {
						return 1;
					} else if (y.getBiFollowersCount() == 1) {
						return -1;
					} else {
						return 0;
					}
				}

			}
		});

		// 使用lambda表达式写法：
		// users.sort(Comparator.comparing(User::getId));
		for (User user : users) {
			System.out.println(user.getId() + " " + user.getCity() + " " + user.getBiFollowersCount());
		}

	}

	/**
	 * 数据结构的转化
	 */
	@Test
	public void test4() {

		List<User> users = new ArrayList<>();
		users.add(new User(0, "张三", 18, "f"));
		users.add(new User(1, "张四", 19, "f"));
		users.add(new User(2, "张五", 19, "f"));
		users.add(new User(3, "老张", 50, "f"));

		// 提取集合中每个对象的属性
		List<String> list = users.stream().map(User::getName).collect(Collectors.toList());

		// 转map
		Map<Integer, Integer> userMap = users.stream().collect(Collectors.toMap(User::getId, item -> item.getId()));
		System.out.println(userMap.get(5));

		// 计算值
		Integer result = users.stream().reduce(0, (sum, item) -> sum + item.getAge(), (i, j) -> i + j);
		// 或者这样写
		// Integer result =
		// userStream.mapToInt(User::getAge).reduce(0,
		// (sum, item) -> sum+item);

		// 转成新的list
		List<com.lzhsite.entity.User> users2 = null;
		users2 = users.stream().map(user -> {
			com.lzhsite.entity.User user2 = new com.lzhsite.entity.User();

			return user2;
		}).collect(Collectors.toList());

	}

	/**
	 * 使用java的lambda表达式实现word count的两种方法
	 */
	@Test
	public void test5() {
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
