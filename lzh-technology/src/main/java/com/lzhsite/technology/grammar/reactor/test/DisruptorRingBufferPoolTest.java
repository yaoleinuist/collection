package com.lzhsite.technology.grammar.reactor.test;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 下午3:17
 * @since 2017/12/22
 */
public class DisruptorRingBufferPoolTest {
    @Test
    public void test_pool() throws InterruptedException {
        int core = Runtime.getRuntime().availableProcessors();
        ExecutorService executors = Executors.newFixedThreadPool(core);
        //创建ringBuffer
        RingBuffer<Order> ringBuffer =
                RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },
                        1024 * 1024,
                        new YieldingWaitStrategy());

        SequenceBarrier barriers = ringBuffer.newBarrier();
        Consumer[] consumers = new Consumer[core];
        for(int i = 0; i < consumers.length; i++){
            consumers[i] = new Consumer("consumer_" + i);
        }

        WorkerPool<Order> workerPool =
                new WorkerPool<Order>(ringBuffer,
                        barriers,
                        new IntEventExceptionHandler(),
                        consumers);


        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        workerPool.start(executors);

        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 2; i++) {
            final Producer producer = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100; j++) {
                        producer.onData(UUID.randomUUID().toString());
                    }
                }
            }).start();
        }
        Thread.sleep(200);
        System.out.println("---------------开始生产-----------------");
        latch.countDown();
        Thread.sleep(1000);
        System.out.println("总数:" + consumers[0].getCount() );
        executors.shutdown();
    }

    class Order {
        private String id;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }
    class Producer {
        RingBuffer<Order> ringBuffer;
        public Producer(RingBuffer<Order> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }
        public void onData(String data) {
            long sequence = ringBuffer.next();
            try {
                Order order = ringBuffer.get(sequence);
                order.setId(data);
            } finally {
                ringBuffer.publish(sequence);
            }
        }
    }
    static AtomicInteger count = new AtomicInteger(0);
    class Consumer implements WorkHandler<Order>{
        String consumerId;
        public Consumer(String consumerId) {
            this.consumerId = consumerId;
        }
        @Override
        public void onEvent(Order event) throws Exception {
            System.out.println("当前消费者: " + this.consumerId + "，消费信息：" + event.getId());
            count.incrementAndGet();
        }
        public int getCount(){
            return count.get();
        }
    }

    static class IntEventExceptionHandler implements ExceptionHandler {
        public void handleEventException(Throwable ex, long sequence, Object event) {}
        public void handleOnStartException(Throwable ex) {}
        public void handleOnShutdownException(Throwable ex) {}
    }
}
