package com.lzhsite.technology.grammar.lambda;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.lzhsite.core.utils.JSONUtil;

//http://www.jdon.com/idea/java/using-optional-effectively-in-java-8.html
public class TestOptional {

	@Test
	public void test1() {
		// orElseGet()
		// 方法类似于orElse()，但是不是直接返回输入参数，而是调用输入参数，返回调用的结果，这个输入参数通常是lambda：
		Stream<String> names = Stream.of("Lamurudu", "Okanbi", "Oduduwa");

		Optional<String> longest = names.filter(name -> name.startsWith("Q")).findFirst();
		String alternate = longest.orElseGet(() -> {
			// perform some interesting code operation
			// then return the alternate value.
			return "Nimrod";

		});
		System.out.println(alternate);

		User user=new User(0, "张三", 18, "f");
		A test=new A();
		test.setLzhcode("lzhcode");
		user.setTest( test);
		String body =JSONUtil.toJson(user);

		String code = Optional.ofNullable(body)
				.map(JSONObject::parseObject)
				.map(sr -> (JSONObject) sr.get("test"))
				.map(Object::toString).orElse(null);

		System.out.println(code);

	}

	@Test
	public void test2() {

		// 如果在T可能空时你需要一个值的话，那么可以使用 orElse()，它能在T值存在的情况下返回这个值，否则返回输入值。
		Stream<String> names = Stream.of("Lamurudu", "Okanbi", "Oduduwa");

		Optional<String> longest = names.filter(name -> name.startsWith("Q")).findFirst();
		String alternate = longest.orElse("Nimrod");
		System.out.println(alternate); // prints out "Nimrod"
	}

	@Test
	public void test3() {

		String endTime = Optional.ofNullable("endTime").orElseThrow(() -> new RuntimeException());

		Optional<Integer> optional1 = Optional.ofNullable(1);
		Optional<Integer> optional2 = Optional.ofNullable(null);

		System.out.println(optional2.orElse(1));

		// isPresent判断值是否存在
		System.out.println(optional1.isPresent());

		// null,不调用Consumer
		optional2.ifPresent(new Consumer<Integer>() {
			@Override
			public void accept(Integer t) {
				System.out.println("value is " + t);
			}
		});
	}

}
