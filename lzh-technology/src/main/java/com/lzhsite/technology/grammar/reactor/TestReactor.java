package com.lzhsite.technology.grammar.reactor;

import reactor.core.publisher.Flux;

//http://www.blogjava.net/DLevin/archive/2015/09/02/427045.html

//类似生产者消费者模式，即有一个或多个生产者将事件放入一个Queue中，而一个或多个消费者主动的从这个Queue中Poll事件来处理；
//而Reactor模式则并没有Queue来做缓冲，每当一个Event输入到Service Handler之后，
//该Service Handler会主动的根据不同的Event类型将其分发给对应的Request Handler来处理。
//Reactor模式的优点
//1。解耦、提升复用性、模块化、可移植性、事件驱动、细力度的并发控制等
//2.线程的切换、同步、数据的移动会引起性能问题。也就是说从性能的角度上，它最大的提升就是减少了性能的使用，
//即不需要每个Client对应一个线程。我的理解，其他业务逻辑处理很多时候也会用到相同的线程，
//IO读写操作相对CPU的操作还是要慢很多，即使Reactor机制中每次读写已经能保证非阻塞读写，这里可以减少一些线程的使用

//Reactor模式的缺点
//1. 相比传统的简单模型，Reactor增加了一定的复杂性，因而有一定的门槛，并且不易于调试。
//2. Reactor模式需要底层的Synchronous Event Demultiplexer支持，比如Java中的Selector支持，操作系统的select系统调用支持，如果要自己实现Synchronous Event Demultiplexer可能不会有那么高效。
//3. Reactor模式在IO读写数据时还是在同一个线程中实现的，即使使用多个Reactor机制的情况下，那些共享一个Reactor的Channel如果出现一个长时间的数据读写，会影响这个Reactor中其他Channel的相应时间，比如在大文件传输时，IO操作就会影响其他Client的相应时间，因而对这种操作，使用传统的Thread-Per-Connection或许是一个更好的选择，或则此时使用Proactor模式。
public class TestReactor {

	
	private static void just() {
        Flux.just("Hello", "spring-reactor").subscribe(System.out::println);
    }

    private static void generate() {
        //这里的写法基本上和JS版没有太大差距了，J8这个版本要闹哪样？
        Flux.generate(consumer -> {
            consumer.next("Hello Flux");
            consumer.complete();
        }).subscribe(System.out::println);
    }

    private static void create() {
        Flux.create(consumer -> {
            for (int i = 0; i < 100; i++) {
                consumer.next(i);
            }
            consumer.complete();
        }).subscribe(System.out::println);
    }

    public static void main(String[] args){
        just();
        generate();
        create();
    }
	
}
