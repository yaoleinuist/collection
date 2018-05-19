package com.lzhsite.technology.grammar.reactor.test;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.core.Fuseable;
import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 下午4:24
 * @since 2017/12/22
 */
public class ReactorTest {

    private static List<String> words = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
    );

    /**
     * Very similarly to how you would create your first Observable in RxJava, you can create a Flux using the just(T…) and fromIterable(Iterable<T>) Reactor factory methods. Remember that given a List, just would just emit the list as one whole, single emission, while fromIterable will emit each element from the iterable list:
     */
    @Test
    public void simpleCreation() {
        Flux<String> fewWords = Flux.just("Hello", "World");
        Flux<String> manyWords = Flux.fromIterable(words);

        fewWords.subscribe(System.out::println);
        System.out.println();
        manyWords.subscribe(System.out::println);
    }

    /**
     * In order to output the individual letters in the fox sentence we'll also need flatMap (as we did in RxJava by Example), but in Reactor we use fromArray instead of from. We then want to filter out duplicate letters and sort them using distinct and sort. Finally, we want to output an index for each distinct letter, which can be done using zipWith and range
     */
    @Test
    public void findingMissingLetter() {
        Flux<String> manyLetters = Flux
                .fromIterable(words)
                .flatMap(word -> Flux.fromArray(word.split("")))
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format("%2d. %s", count, string));

        manyLetters.subscribe(System.out::println);
    }

    /**
     * The previous article noted the resemblance between the Rx vocabulary and the Streams API, and in fact when the data is readily available from memory, Reactor, like Java Streams, acts in simple push mode (see the backpressure section below to understand why). More complex and truly asynchronous snippets wouldn't work with this pattern of just subscribing in the main thread, primarily because control would return to the main thread and then exit the application as soon as the subscription is done. For instance:
     */
    @Test
    public void shortCircuit() {
        Flux<String> helloPauseWorld =
                Mono.just("Hello")
                        .concatWith(Mono.just("world")
                                .delaySubscription(Duration.ofMillis(500)));//Millis

        helloPauseWorld.subscribe(System.out::println);
    }

    /**
     * The second way you could solve that issue is by using one of the operators that revert back to the non-reactive world. Specifically, toIterable and toStream will both produce a blocking instance. So let's use toStream for our example:
     */
    @Test
    public void blocks() {
        Flux<String> helloPauseWorld =
                Mono.just("Hello")
                        .concatWith(Mono.just("world")
                                .delaySubscription(Duration.ofMillis(500)));//Millis

        helloPauseWorld.toStream()
                .forEach(System.out::println);
    }

    /**
     * As we mentioned above, RxJava amb() operator has been renamed firstEmitting (which more clearly hints at the operator's purpose: selecting the first Flux to emit). In the following example, we create a Mono whose start is delayed by 450ms and a Flux that emits its values with a 400ms pause before each value. When firstEmitting() them together, since the first value from the Flux comes in before the Mono's value, it is the Flux that ends up being played:
     */
    @Test
    public void firstEmitting() {
        Mono<String> a = Mono.just("oops I'm late")
                .delaySubscription(Duration.ofMillis(450));
        Flux<String> b = Flux.just("let's get", "the party", "started")
                .delayElements(Duration.ofMillis(400));
//                .delayMillis(400);

        Flux.first(a, b).toIterable().forEach(System.out::println);
//        Flux.firstEmitting(a, b)
//                .toIterable()
//                .forEach(System.out::println);
    }

    /**
     * The last signature of the subscribe method includes a custom Subscriber (shown later in this section), which shows how to attach a custom Subscriber, as shown in the following example:
     */
    @Test
    public void test_custom_sub(){
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println("src : " + i),
                error -> System.err.println("Error " + error),
                () -> {
                    System.out.println("Done");
                },
                s -> ss.request(10));
        ints.subscribe(ss);
    }
    @Test
    public void test_125(){
        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .map(i -> (i+100))
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
    }
    @Test
    public void test_138(){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
    @Test
    public void test_139() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Flux.<Long>
                interval(Duration.ofMillis(1),Duration.ofMillis(1),
                    Schedulers.fromExecutor(Executors.newScheduledThreadPool(8)))
                .delaySubscription(Duration.ofMillis(ThreadLocalRandom.current().nextInt(0, 1000)))
                .doFinally(signalType -> {
                    System.out.println("type : " + signalType);
                    latch.countDown();
                })
                .subscribeOn(Schedulers.elastic())
                .subscribe(i -> System.out.println(format.format(new Date()) + " >>> " + Thread.currentThread().getName() + " -> " + i));

        latch.await();
    }
    @Test
    public void test_160(){
        WorkQueueProcessor<String> processor = WorkQueueProcessor.<String>builder().bufferSize(1024).share(true).build();
        Flux<String> hot = processor.publish().autoConnect().map(String::toUpperCase);
        FluxSink<String> sink = processor.sink();
        hot.subscribe(x -> System.out.println("one : " + x));
        for (int i = 0; i < 2; i++) {
            sink.next("s_"+i);
            processor.onNext("a_"+i);
        }

        hot.subscribe(x -> System.out.println("two : " + x));
        for (int i = 0; i < 2; i++) {
            processor.onNext("b_"+i);
        }
        processor.subscribe(x -> System.out.println("processor : " + x));
        hot.doOnComplete(() -> System.out.println("completed."));
        processor.awaitAndShutdown();
    }

    @Test
    public void test_182(){
        Flux.range(1, 10).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(System.out::println);
    }
    @Test
    public void test_191(){
        //Returns a power of two list
        Flux.range(1, 10).reduce(new HashMap<String, List<Integer>>(16), (accumulator, integer) -> {
            boolean pow2 = Integer.bitCount(integer) == 1;
            accumulator.putIfAbsent("power", new ArrayList<>());
            accumulator.putIfAbsent("other", new ArrayList<>());
            if(pow2) {
                accumulator.get("power").add(integer);
            } else {
                accumulator.get("other").add(integer);
            }
            return accumulator;
        }).subscribe(System.out::println);
    }
    @Test
    public void test_106(){
        Flux.merge(Flux.range(1, 10), Flux.range(10, 20)).buffer()
                .retryWhen(throwableFlux -> throwableFlux.zipWith(Flux.range(1, 3), (throwable, integer) -> integer).flatMap(i -> Flux.interval(Duration.ofMillis(i))))
                .subscribe(System.out::println);
        ;
    }
    @Test
    public void test_215() {
        Flux<String> flux =
                Flux.<String>error(new IllegalArgumentException())
                        .retryWhen(companion -> companion
                                .zipWith(Flux.range(1, 4),
                                        (error, index) -> {
                                            if (index < 4) return index;
                                            else throw Exceptions.propagate(error);
                                        })
                        );
        flux.subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription subscription) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("next");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("com");
            }
        });
    }
    @Test
    public void test_134(){
        Flux.range(1, 10)
                .map(i -> (i+100))
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
    }
    @Test
    public void test_186(){
        Flux.range(1, 10).buffer(5).flatMap(list -> {
            long sum = 0;
            for(long x : list){
                sum += x;
            }
            return Flux.just(sum);
        }).collect(Collectors.summingLong(Long::longValue)).subscribe(System.out::println);
    }
    @Test
    public void test_198(){
        Flux.<Integer>push(c -> {
            for (int i = 0; i < 10; i++) {
                c.next(i);
            }
            c.complete();
        }).subscribe(System.out::println);
    }
    @Test
    public void test_206(){
        Flux.<Integer>create(c -> {
            for (int i = 0; i < 10; i++) {
                c.next(i);
            }
            c.complete();
        }).subscribe(System.out::println);
    }

    @Test
    public void test_140(){
        Flux.range(1, 10)
                .doAfterTerminate(() -> System.out.println("doAfterTerminate"))
                .subscribe(System.out::println);
    }
    @Test
    public void test_146(){

        Flux.fromIterable(Arrays.<Integer>asList(1,3,2,4,5,3,2)).distinct()
                .doAfterTerminate(() -> System.out.println("doAfterTerminate"))
                .subscribe(System.out::println);

    }

    class DistinctFluxOperator<T> extends FluxOperator<T, T> {
        protected DistinctFluxOperator(Flux<? extends T> source) {
            super(source);
        }
        @Override
        public void subscribe(CoreSubscriber<? super T> actual) {
            source.subscribe(new DistinctSubscriber<T>(actual));
        }
    }
    class DistinctSubscriber<T> implements Fuseable.ConditionalSubscriber<T> {
        CoreSubscriber actual;
        Set<T> keyMemory = new HashSet<T>();
        public DistinctSubscriber(CoreSubscriber<? super T> actual) {
            this.actual = actual;
        }
        Fuseable.QueueSubscription<T> qs;
        boolean done;
        @Override
        public void onSubscribe(Subscription s) {
//            if (Operators.validate(this.qs, s)) {
//                this.qs = (Fuseable.QueueSubscription<T>) s;
//                actual.onSubscribe(this);
//            }
            actual.onSubscribe(s);
        }

        @Override
        public void onNext(T t) {
            if(keyMemory.add(t)) {
                actual.onNext(t);
            } else {
                qs.request(1);
            }
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                Operators.onErrorDropped(t, actual.currentContext());
                return;
            }
            done = true;
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            actual.onComplete();
        }

        @Override
        public boolean tryOnNext(T t) {
            if(keyMemory.add(t)) {
                actual.onNext(t);
                return true;
            }
            return false;
        }
    }
    /**
     * The SampleSubscriber class extends BaseSubscriber, which is the recommended abstract class for user-defined Subscribers in Reactor. The class offers hooks that can be overridden to tune the subscriber’s behavior. By default, it will trigger an unbounded request and behave exactly like subscribe(). However, extending BaseSubscriber is much more useful when you want a custom request amount.
     * The bare minimum implementation is to implement both hookOnSubscribe(Subscription subscription) and hookOnNext(T value). In this case, the hookOnSubscribe method prints a statement to standard out and makes the first request. Then the hookOnNext method prints a statement and processes each of the remaining requests, one request at a time.
     * @param <T>
     */
    class SampleSubscriber<T> extends BaseSubscriber<T> {
        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("hook Subscribe");
            request(1);
        }
        public void hookOnNext(T value) {
            System.out.println("hook value : " + value);
            request(1);
        }
        @Override
        protected void hookOnComplete() {
            System.out.println("hook Complete");
            request(1);
        }
    }

    @Test
    public void test_generate() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });

        flux.subscribe(System.out::println);
    }
    @Test
    public void test_generate_atomic() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                });

        flux.subscribe(System.out::println);
    }

    /**
     * If your state object needs to clean up some resources, use the generate(Supplier<S>, BiFunction, Consumer<S>) variant to clean up the last state instance.
     */
    @Test
    public void test_generate_atomic_clean() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                }
                , (state) -> System.out.println("state: " + state));

        flux.subscribe(System.out::println);
    }

    @Test
    public void test_expandDeep() {
        Flux.just(1, 2)
                .expandDeep(integer -> Flux.just(integer + 1), 2)
                .map(integer -> {
                    if (integer > 100) {
                        throw new RuntimeException("max");
                    }
                    return integer;
                }).subscribe(System.out::println);
    }
    @Test
    public void test_441(){
        Flux.just(1,2).elapsed().subscribe(System.out::println);
    }

    @Test
    public void test_onBackpressureBuffer() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);


        Flux.interval(Duration.ofMillis(1), Duration.ofMillis(1))
                .subscribeOn(Schedulers.elastic())
                .onBackpressureBuffer(10, aLong -> {
                    System.out.println(Thread.currentThread().getName() + " | " + " 丢弃：" + aLong);
                },BufferOverflowStrategy.DROP_OLDEST).subscribe(new Subscriber<Long>() {
            AtomicLong index = new AtomicLong();

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("sub = " + subscription);
//                subscription.request(100);
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Long aLong) {
//                index.incrementAndGet();
//                System.out.println(Thread.currentThread().getName() + " | " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " >> " + aLong);
//                if(index.get() == 100) {
//                    latch.countDown();
//                }
                try {
                    TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(30,100));
                } catch (InterruptedException e) {
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(System.currentTimeMillis() + " >> " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println(System.currentTimeMillis() + " >> " + "done.");
            }
        });

        latch.await();


    }


    @Test
    public void test_on() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    System.out.println("生产 >>> " + Thread.currentThread());
                    long i = state.getAndIncrement();
                    sink.next(i * 2);
                    if (i == 3) sink.complete();
                    return state;
                })
                .subscribeOn(Schedulers.parallel(), true)
                .publishOn(Schedulers.elastic())
                .map(i -> {
                    System.out.println("转换 >>> " + Thread.currentThread());
                    return String.valueOf(i);
                })
                .doAfterTerminate(latch::countDown)
                .subscribe(i -> System.out.println("消费 >>> " + Thread.currentThread()));

        latch.await();
    }

}
