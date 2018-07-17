package com.lzhsite.technology.grammar.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

// http://tianmaotalk.iteye.com/blog/2273314
public class TestLabmda {

	private static List<User> users = new ArrayList<>();

	static {
		users.add(new User(0, "张三", 18, "f"));
		users.add(new User(1, "张四", 19, "f"));
		users.add(new User(2, "张五", 19, "f"));
		users.add(new User(3, "老张", 50, "f"));
	}

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

		//////////////////////////////////////////////////////////////////////////////////////////////// 
		List<Integer> list = Arrays.asList(1, 12, 21, 3, 13);
		System.out.println("sort前：" + list);
		Collections.sort(list);
		System.out.println("使用传统特性sort后：" + list);
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}

		});
		System.out.println("使用传统特性sort后：" + list);


		Collections.sort(list, (a, b) -> b.compareTo(a));
		System.out.println("使用lambda表达式降序排序后：" + list);
		list.sort((a, b) -> a.compareTo(b));
		System.out.println("使用lambda表达式升序排序后：" + list);

	}

	/**
	 * 数据结构的转化
	 */
	@Test
	public void test4() {

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
		users2 = Optional.ofNullable(users).orElse(new ArrayList<>()).stream().map(user -> {
			com.lzhsite.entity.User user2 = new com.lzhsite.entity.User();

			return user2;
		}).collect(Collectors.toList());

	}

	/**
	 * list转map
	 */
	@Test
	public void test5() {
		// 重复问题
		Map<Integer, User> keyRedo = users.stream()
				.collect(Collectors.toMap(User::getId, Function.identity(), (key1, key2) -> key2));
		// 方式二：指定实例的map
		Map<Integer, User> linkedHashMap = users.stream()
				.collect(Collectors.toMap(User::getId, temp -> temp, (key1, key2) -> key2, LinkedHashMap::new));

		System.out.println(keyRedo);
		System.out.println(linkedHashMap);
	}

	/**
	 * 分组
	 */
	@Test
	public void test6() {
		List<Integer> list = new ArrayList() {
			{
				add(12);
				add(20);
				add(12);
				add(22);
				add(22);
				add(23);
				add(159);
				add(12);
			}
		};


		// 分组统计
		Map<Integer, Long> stastic = list.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		stastic.forEach((k, v) -> System.out.println(k + ":" + v));

		// 字段分组
		Map<String, List<User>> usersByGender = users.stream().collect(Collectors.groupingBy(User::getGender));

		// 条件分组
		Map<String, List<User>> tripleUsers = users.stream()
				.collect(Collectors.groupingBy((Function<User, String>) user -> {
					String key;
					if (user.getAge() <= 20) {
						key = "less20";
					} else if (user.getAge() <= 40) {
						key = "less40";
					} else {
						key = "more40";
					}
					return key;
				}, Collectors.toList()));

		// 嵌套分组
		Map<String, Map<Integer, List<User>>> mulity = users.stream()
				.collect(Collectors.groupingBy(User::getName, Collectors.groupingBy(User::getAge)));
 
	
	}
}
