package com.lzhsite.technology.grammar.reactor.test;

import com.codahale.metrics.*;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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
 * @version 上午10:17
 * @since 2017/12/29
 */
public class EventStream2Test {
    private static final Logger logger = LoggerFactory.getLogger(EventStream2Test.class);

    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        EventStream2Test test = new EventStream2Test();
        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = test.newProjectionMetrics(metricRegistry, new ScheduledReporter[]{
                test.newSlf4jReporter(metricRegistry)
//                ,test.newGraphiteReporter(metricRegistry, host, port)
        });
        EventConsumer client = test.newClientProjection(metrics);
        EventConsumer f = test.newFailOnConcurrentModification(client);
        EventConsumer w = test.newWaitOnConcurrentModification(client, metricRegistry);
        EventStream es = test.newEventStream();

//        es.simpleConsume(client);
//        es.callableConsume(client);
//        es.parallelConsume(client);
        es.groupConsume(client);
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
    Slf4jReporter newSlf4jReporter(MetricRegistry registry){
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        return reporter;
    }
    class EventStream {

        void simpleConsume(EventConsumer consumer) {
            observe().subscribe(consumer::consume, e -> logger.error("Fatal error", e));
        }
        void callableConsume(EventConsumer consumer) {
            observe()
                    .flatMap(e -> Flux.just(e).subscribeOn(Schedulers.elastic()).map(consumer::consume))
                    .buffer(Duration.ofSeconds(1))
                    .flatMap(events -> Flux.just(events.size()))
                    .subscribe(
                            c -> logger.info("Processed {} events/s", c),
                            e -> logger.error("Fatal error", e)
                    );
        }
        void parallelConsume(EventConsumer consumer) {
            observe()
                    .parallel(20)
                    .runOn(Schedulers.newElastic("consume-parallel-thread"))
                    .map(consumer::consume)
                    .doOnError(e -> logger.error("Fatal error", e))
                    .subscribe();
        }
        void groupConsume(EventConsumer consumer) {
            observe()
                    .groupBy(Event::getId)
                    .flatMap(group -> group.publishOn(Schedulers.elastic()).map(consumer::consume))
                    .buffer(Duration.ofSeconds(1))
                    .flatMap(events -> Flux.just(events.size()))
//                    .collect(Collectors.summingLong(List::size))
                    .subscribe(
                            c -> logger.info("Processed {} events/s", c),
                            e -> logger.error("Fatal error", e)
                    );
        }
        void distinctConsume(EventConsumer consumer) {
            observe()
                    .distinct(Event::getUuid, () -> recentUUIDCache(Duration.ofSeconds(10)))
                    .groupBy(Event::getId)
                    .flatMap(group -> group.publishOn(Schedulers.elastic()).map(consumer::consume))
                    .buffer(Duration.ofSeconds(1))
                    .flatMap(events -> Flux.just(events.size()))
                    .subscribe(
                            c -> logger.info("Processed {} events/s", c),
                            e -> logger.error("Fatal error", e)
                    );
        }

        Flux<Event> observe() {
            return Flux.<Long>
                    interval(Duration.ofMillis(1))
                    .delaySubscription(Duration.ofMillis(ThreadLocalRandom.current().nextInt(0, 1000)))
                    .map(x -> new Event(ThreadLocalRandom.current().nextInt(1000, 1100), UUID.randomUUID()))
                    .flatMap(this::occasionallyDuplicate)
                    .subscribeOn(Schedulers.elastic());
        }
        Flux<Event> occasionallyDuplicate(Event x) {
            Flux<Event> event = Flux.just(x);
            if(Math.random() >= 0.01) {
                return event;
            }
            Flux<Event> duplicated = event.delayElements(Duration.ofMillis(ThreadLocalRandom.current().nextInt(5, 20)));
            return event.concatWith(duplicated);
        }
    }
    Set<UUID> recentUUIDCache(Duration duration) {
        return Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(duration.toMillis(), TimeUnit.MILLISECONDS).<UUID, Boolean>build().asMap());
    }

    class ClientProjection implements EventConsumer {
        private final ProjectionMetrics metrics;
        public ClientProjection(ProjectionMetrics metrics) {
            this.metrics = metrics;
        }
        @Override
        public Event consume(Event event) {
//            logger.info("消费event : {}", event);
            metrics.meter();
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
    class ProjectionMetrics {
        private final Histogram latencyHist;
        private final Meter latencyMeter;
        ProjectionMetrics(MetricRegistry metricsRegistry, ScheduledReporter[] reporters) {
            Objects.requireNonNull(reporters, "ScheduledReporter array is must one");
            for (ScheduledReporter reporter: reporters) {
                reporter.start(1, TimeUnit.SECONDS);
            }
            latencyHist = metricsRegistry.histogram(MetricRegistry.name(ProjectionMetrics.class, "latency"));
            latencyMeter = metricsRegistry.meter(MetricRegistry.name(ProjectionMetrics.class, "meter"));
        }
        void latency(Duration duration) {
            latencyHist.update(duration.toMillis());
        }
        void meter(){
            latencyMeter.mark();
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
    interface EventConsumer {
        Event consume(Event event);
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


}
