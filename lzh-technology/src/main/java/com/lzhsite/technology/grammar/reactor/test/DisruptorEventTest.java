package com.lzhsite.technology.grammar.reactor.test;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>orcTest
 * <p>com.stony.orc
 *
 * @author stony
 * @version 上午10:53
 * @since 2017/12/22
 */
public class DisruptorEventTest {

    /**
     *  BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；
        SleepingWaitStrategy 的性能表现跟 BlockingWaitStrategy 差不多，对 CPU 的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；
        YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于 CPU 逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性。
     */
    @Test
    public void test_disruptor() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
        WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
        WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();

        EventFactory<LongEvent> eventFactory = new LongEventFactory();
        int ringBufferSize = 1024 * 1024;
        Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(eventFactory,
                ringBufferSize, executor, ProducerType.SINGLE, YIELDING_WAIT);

        EventHandler<LongEvent> eventHandler = new LongEventHandler();
        EventHandler<LongEvent> afterEventHandler = new LongAfterEventHandler();

        disruptor.handleEventsWith(eventHandler).then(afterEventHandler);
        //1.启动 Disruptor
        disruptor.start();

        //2.发布事件 ->  事件只有在提交之后才会通知 EventProcessor 进行处理
        AtomicLong data = new AtomicLong();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();


        LongEventProducer producer = new LongEventProducer(ringBuffer);
        producer.onData(data.incrementAndGet()); //发布第一个事件

        Translator TRANSLATOR = new Translator();
        ringBuffer.publishEvent(TRANSLATOR, data.incrementAndGet()); //发布第二个事件

        for (int i = 0; i < 10; i++) {
            producer.onData(data.incrementAndGet()); //发布事件
        }

        System.out.println("-------------");
        System.out.println(ringBuffer.remainingCapacity());
        disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        executor.shutdown();//关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；
        System.out.println("-------------");
    }
    class LongEventProducer {
        RingBuffer<LongEvent> ringBuffer;
        public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }
        public void onData(long value) {
            //2.1.先从 RingBuffer 获取下一个可以写入的事件的序号；
            long sequence = ringBuffer.next();
            try{
                //2.2.获取对应的事件对象，将数据写入事件对象；
                LongEvent event = ringBuffer.get(sequence);
                //业务数据
                event.set(value);
            } finally {
                //2.3.将事件提交到 RingBuffer;
                ringBuffer.publish(sequence); //发布事件
            }
        }
    }

    /**
     * 定义一个事件， 通过 Disruptor 进行交换的数据类型
     */
    class LongEvent {
        private long value;
        public void set(long value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return "LongEvent{" +
                    "value=" + value +
                    '}';
        }
    }

    /**
     * 定义事件工厂
     */
    class LongEventFactory implements EventFactory<LongEvent> {
        public LongEvent newInstance() {
            return new LongEvent();
        }
    }

    /**
     * 定义事件处理的具体实现
     */
    class LongEventHandler implements EventHandler<LongEvent> {
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
            System.out.println("Event: " + event);
            event.set(event.value + 100);
        }
    }
    class LongAfterEventHandler implements EventHandler<LongEvent> {
        public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
            System.out.println("After Event: " + event);
        }
    }
    class Translator implements EventTranslatorOneArg<LongEvent, Long>{
        @Override
        public void translateTo(LongEvent event, long sequence, Long data) {
            event.set(data);
        }
    }
}
