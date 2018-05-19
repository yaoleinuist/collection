package com.lzhsite.technology.grammar.reactor.test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 上午10:23
 * @since 2017/12/22
 */
public class DisruptorTest {

    @Test
    public void test_14() throws InterruptedException {
        Executor executor = Executors.newCachedThreadPool();

        Disruptor<MyEvent> disruptor = new Disruptor<MyEvent>(EVENT_FACTORY, 32, executor);
        BatchHandler handler1 = new BatchHandler();
        BatchHandler handler2 = new BatchHandler();
        disruptor.handleEventsWith(handler1);
        disruptor.after(handler1).handleEventsWith(handler2);

        RingBuffer<MyEvent> ringBuffer = disruptor.start();
        System.out.println(ringBuffer);

        long sequence = ringBuffer.next();
        MyEvent event = ringBuffer.get(sequence);
//        event.value = 100;
        ringBuffer.publish(sequence);
        System.out.println("---------    ");
    }

    class BatchHandler implements EventHandler<MyEvent> {
        @Override
        public void onEvent(MyEvent event, long sequence, boolean endOfBatch) throws Exception {
            System.out.println("event : " + event);
            System.out.println(sequence);
            System.out.println(endOfBatch);
        }
    }

    class MyEvent {
        int value;
        public MyEvent(int value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "{value : " + value + "}";
        }
    }

    public final EventFactory<MyEvent> EVENT_FACTORY = new EventFactory<MyEvent>() {
        public MyEvent newInstance() {
            return new MyEvent(ThreadLocalRandom.current().nextInt(1, 100));
        }
    };
    
    

}
