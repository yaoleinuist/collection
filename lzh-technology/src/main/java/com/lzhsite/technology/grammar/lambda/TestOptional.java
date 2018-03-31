package com.lzhsite.technology.grammar.lambda;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

//http://www.jdon.com/idea/java/using-optional-effectively-in-java-8.html
public class TestOptional {

	@Test
	public void test1() {
		//　orElseGet() 方法类似于orElse()，但是不是直接返回输入参数，而是调用输入参数，返回调用的结果，这个输入参数通常是lambda：
		Stream<String> names = Stream.of("Lamurudu", "Okanbi", "Oduduwa");

		Optional<String> longest = names.filter(name -> name.startsWith("Q")).findFirst();
		String alternate = longest.orElseGet(() -> {
			// perform some interesting code operation
			// then return the alternate value.
			return "Nimrod";

		});
		System.out.println(alternate);

	}

	@Test
	public void test2() {

		// 如果在T可能空时你需要一个值的话，那么可以使用 orElse()，它能在T值存在的情况下返回这个值，否则返回输入值。
		Stream<String> names = Stream.of("Lamurudu", "Okanbi", "Oduduwa");

		Optional<String> longest = names
				.filter(name -> name.startsWith("Q"))
				.findFirst();
		String alternate = longest.orElse("Nimrod");
		System.out.println(alternate); // prints out "Nimrod"
	}

}
