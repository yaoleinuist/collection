package com.lzhsite.technology.grammar.reactor.test;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 上午11:31
 * @since 2017/12/27
 */
public class EventStreamTest {

    private static final Logger logger = LoggerFactory.getLogger(EventStreamTest.class);

    public static void main(String[] args) throws InterruptedException {
        String host = "10.0.11.239";
        int port = 2003;
        CountDownLatch latch = new CountDownLatch(1);
        EventStreamTest test = new EventStreamTest();

        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = test.newProjectionMetrics(metricRegistry, new ScheduledReporter[]{
                test.newSlf4jReporter(metricRegistry)
//                ,test.newGraphiteReporter(metricRegistry, host, port)
        });
        EventConsumer client = test.newClientProjection(metrics);


        EventConsumer f = test.newFailOnConcurrentModification(client);
        EventConsumer w = test.newWaitOnConcurrentModification(client, metricRegistry);
        EventStream es = test.newEventStream();

//        es.consume(client);
//        es.simpleConsume(f);
//        es.callableConsume(w);
//        es.groupConsume(f);

        es.distinctEventConsume(f, metricRegistry);
        latch.await();
    }

    EventStream newEventStream() {
        return new EventStream();
    }
    ProjectionMetrics newProjectionMetrics(MetricRegistry metricRegistry, ScheduledReporter[] reporters){
        return new ProjectionMetrics(metricRegistry,reporters);
    }
    EventConsumer newClientProjection(ProjectionMetrics metrics){
        return new ClientProjection(metrics);
    }
    EventConsumer newFailOnConcurrentModification(EventConsumer eventConsumer){
        return new FailOnConcurrentModification(eventConsumer);
    }
    EventConsumer newWaitOnConcurrentModification(EventConsumer eventConsumer, MetricRegistry registry) {
        return new WaitOnConcurrentModification(eventConsumer, registry);
    }
    DistinctEvent newDistinctEvent(Duration duration, MetricRegistry metricRegistry) {
        return new DistinctEvent(duration, metricRegistry);
    }
    CompareRatio newCompareRatio(MetricRegistry metricRegistry, Meter consumers, Meter producers) {
        CompareRatio compareRatio = new CompareRatio(consumers, producers);
        metricRegistry.register("生产者消费者比率", compareRatio);
        return compareRatio;
    }
    Slf4jReporter newSlf4jReporter(MetricRegistry registry){
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        return reporter;
    }
    GraphiteReporter newGraphiteReporter(MetricRegistry registry, String host, int port){
        final Graphite graphite = new Graphite(new InetSocketAddress(host, port));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(host)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        return reporter;
    }
    class EventStream {
        void consume(EventConsumer consumer) {
            observe().subscribe(consumer::consume, e -> logger.error("Error emitting event.", e));
        }
        void simpleConsume(EventConsumer consumer) {
            observe().subscribe(consumer::consume, e -> logger.error("Fatal error", e));
        }

        /**
         * 异步消费 flatMap -> Observable.fromCallable
         * @param consumer
         */
        void callableConsume(EventConsumer consumer) {
            observe()
                .flatMap(event -> Observable.fromCallable(() -> consumer.consume(event)).subscribeOn(Schedulers.io()))
                .window(1, TimeUnit.SECONDS)
                .flatMap(Observable::count)
                .subscribe(
                        c -> logger.info("Processed {} events/s", c),
                        e -> logger.error("Fatal error", e));
        }
        /**
         * 用Id对event进行分组，
         * 将单一的Observable流分割成多个流
         * 收集1 秒内处理的event并计数
         * @param consumer
         */
        void groupConsume(EventConsumer consumer) {
            observe()
                    .groupBy(Event::getId)
                    .flatMap(group -> group.observeOn(Schedulers.io()).map(consumer::consume))
                    .window(1, TimeUnit.SECONDS)
                    .flatMap(Observable::count)
                    .subscribe(
                            c -> logger.info("Processed {} events/s", c),
                            e -> logger.error("Fatal error", e));
        }

        /**
         * observe一个event流
         * 消除重复的UUID
         * 依据clientId对event分组
         * 对每一个client有序地处理event
         * 收集1 秒内处理的event并计数
         * @param consumer
         * @param registry
         */
        void distinctEventConsume(EventConsumer consumer, MetricRegistry registry) {
            observe()
                    .lift(new DistinctEvent(Duration.ofSeconds(10), registry))
                    .groupBy(Event::getId)
                    .flatMap(group -> group.observeOn(Schedulers.io()).map(consumer::consume))
                    .window(1, TimeUnit.SECONDS)
                    .flatMap(Observable::count)
                    .subscribe(
                            c -> logger.info("Processed {} events/s", c),
                            e -> logger.error("Fatal error", e));
        }


        /**
         * interval 每毫秒输出一个long稳定流，
         * delay 对每个event进行0～1000毫秒的随机延迟
         * map 将long 映射成一个Event对象，每个对象包含1000~1100的id
         * flatMap 将每个event映射到自身(99%情况下),然而剩余1%的情况，将event 返回两次，第二次出现的时间随机延迟5~20毫秒
         * @return
         */
        Observable<Event> observe() {
            return Observable.interval(1, TimeUnit.MILLISECONDS)
                    .delay(x -> Observable.timer(ThreadLocalRandom.current().nextInt(0, 1000),TimeUnit.MILLISECONDS))
                    .map(x -> new Event(ThreadLocalRandom.current().nextInt(1000, 1100), UUID.randomUUID()))
                    .flatMap(this::occasionallyDuplicate)
                    .subscribeOn(Schedulers.io());
        }

        private Observable<Event> occasionallyDuplicate(Event x) {
            final Observable<Event> event = Observable.just(x);
            if(Math.random() >= 0.01) {
                return event;
            }
            final Observable<Event> duplicated = event.delay(ThreadLocalRandom.current().nextInt(5, 20), TimeUnit.MILLISECONDS);
            return event.concatWith(duplicated);
        }
    }
    @FunctionalInterface
    interface EventConsumer {
        Event consume(Event event);
        default Observable<Event> defaultConsume(Event event) {
            return Observable.fromCallable(() -> this.consume(event)).subscribeOn(Schedulers.io());
        }
    }
    class ClientProjection implements EventConsumer {
        private final ProjectionMetrics metrics;
        public ClientProjection(ProjectionMetrics metrics) {
            this.metrics = metrics;
        }
        @Override
        public Event consume(Event event) {
            metrics.latency(Duration.between(event.getCreated(), Instant.now()));
            //模拟业务处理
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 15));
            } catch (InterruptedException e) {
                throw new RuntimeException("ClientProjection Interrupted :", e);
            }
            return event;
        }
    }
    class FailOnConcurrentModification implements EventConsumer {
        final EventConsumer downstream;
        final ConcurrentMap<Integer, Lock> locks = new ConcurrentHashMap<>(512);
        public FailOnConcurrentModification(EventConsumer downstream) {
            this.downstream = downstream;
        }
        @Override
        public Event consume(Event event) {
            final Lock lock = findLock(event);
            if(lock.tryLock()) {
                try {
                    downstream.consume(event);
                } finally {
                    lock.unlock();
                }
            } else {
                logger.error("Event {} already begin modified by another thread.", event.getId());
            }
            return event;
        }
        Lock findLock(Event event) {
            return locks.computeIfAbsent(event.getId(), id -> new ReentrantLock());
        }
    }
    class WaitOnConcurrentModification implements EventConsumer {
        final EventConsumer downstream;
        final Timer lockWait;
        final ConcurrentMap<Integer, Lock> locks = new ConcurrentHashMap<>(512);
        public WaitOnConcurrentModification(EventConsumer downstream, MetricRegistry registry) {
            this.downstream = downstream;
            this.lockWait = registry.timer(MetricRegistry.name(WaitOnConcurrentModification.class,"lockWait"));
        }
        @Override
        public Event consume(Event event) {
            try {
                final Lock lock = findLock(event);
                final Timer.Context time = lockWait.time();
                try {
                    final boolean locked = lock.tryLock(1, TimeUnit.SECONDS);
                    time.close();
                    if (locked) {
                        downstream.consume(event);
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                logger.warn("Event[" + event + "] Interrupted.");
            }
            return event;
        }
        Lock findLock(Event event) {
            return locks.computeIfAbsent(event.getId(), id -> new ReentrantLock());
        }
    }
    class ProjectionMetrics {
        private final Histogram latencyHist;
        ProjectionMetrics(MetricRegistry metricsRegistry, ScheduledReporter[] reporters) {
            Objects.requireNonNull(reporters, "ScheduledReporter array is must one");
            for (ScheduledReporter reporter: reporters) {
                reporter.start(1, TimeUnit.SECONDS);
            }
            latencyHist = metricsRegistry.histogram(MetricRegistry.name(ProjectionMetrics.class, "latency"));
        }
        void latency(Duration duration) {
            latencyHist.update(duration.toMillis());
        }
    }

    class Event {
        final Instant created = Instant.now();
        int id;
        UUID uuid;
        public Event(int id, UUID uuid) {
            this.id = id;
            this.uuid = uuid;
        }
        public int getId() {
            return id;
        }
        public UUID getUuid() {
            return uuid;
        }
        public Instant getCreated() {
            return created;
        }
        @Override
        public String toString() {
            return "Event{" +
                    "id=" + id +
                    ", uuid='" + uuid + '\'' +
                    '}';
        }
    }

    /**
     * 利用cache缓存，去除N秒内重复的UUID
     * @see rx.internal.operators.OperatorDistinct
     */
    class DistinctEvent implements Observable.Operator<Event, Event> {
        final Duration duration;
        final Meter duplicates;
        public DistinctEvent(Duration duration, MetricRegistry metricRegistry) {
            this.duration = duration;
            this.duplicates = metricRegistry.meter(MetricRegistry.name(DistinctEvent.class, "duplicates"));
        }
        @Override
        public Subscriber<? super Event> call(Subscriber<? super Event> child) {
            return new Subscriber<Event>() {
//                Map<UUID, Boolean> cache = CacheBuilder.newBuilder()
//                        .expireAfterWrite(duration.toMillis(),TimeUnit.MILLISECONDS)
//                        .<UUID,Boolean>build()
//                        .asMap();
                Set<UUID> keyMemory = recentUUIDCache(duration);
                @Override
                public void onCompleted() {
//                    cache = null;
                    keyMemory = null;
                    child.onCompleted();
                }
                @Override
                public void onError(Throwable e) {
//                    cache = null;
                    keyMemory = null;
                    child.onError(e);
                }
                @Override
                public void onNext(Event event) {
                    if (keyMemory.add(event.getUuid())) {
                        child.onNext(event);
                    } else {
                        duplicates.mark();
                        request(1);
                    }
//                    if (cache.put(event.getUuid(), true) == null) {
//                        child.onNext(event);
//                    } else {
//                        duplicates.mark();
//                        request(1);
//                    }
                }
            };
        }
    }
    Set<UUID> recentUUIDCache(Duration duration) {
        return Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(duration.toMillis(), TimeUnit.MILLISECONDS).<UUID, Boolean>build().asMap());
    }
    class CompareRatio extends RatioGauge {
        final Meter consumers;
        final Meter producers;
        public CompareRatio(Meter consumers, Meter producers) {
            this.consumers = consumers;
            this.producers = producers;
        }
        @Override
        protected Ratio getRatio() {
            return Ratio.of(consumers.getOneMinuteRate(),
                    producers.getOneMinuteRate());
        }
    }

}
