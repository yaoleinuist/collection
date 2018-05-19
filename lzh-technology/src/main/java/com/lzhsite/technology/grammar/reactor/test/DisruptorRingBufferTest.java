package com.lzhsite.technology.grammar.reactor.test;

import com.lmax.disruptor.*;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * <p>orcTest
 * <p>com.stony.orc
 * 消息中间件
 * @author stony
 * @version 上午11:28
 * @since 2017/12/22
 */
public class DisruptorRingBufferTest {

    @Test
    public void test_ring_buffer() throws ExecutionException, InterruptedException {
        int BUFFER_SIZE = 1024;
        int THREAD_NUMBERS = 4;

        /*
         * createSingleProducer创建一个单生产者的RingBuffer，
         * 第一个参数叫EventFactory，从名字上理解就是"事件工厂"，其实它的职责就是产生数据填充RingBuffer的区块。
         * 第二个参数是RingBuffer的大小，它必须是2的指数倍 目的是为了将求模运算转为&运算提高效率
         * 第三个参数是RingBuffer的生产都在没有可用区块的时候(可能是消费者（或者说是事件处理器） 太慢了)的等待策略
         */
        final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        }, BUFFER_SIZE, new YieldingWaitStrategy());

        //1.创建线程池
        ExecutorService executors = Executors.newFixedThreadPool(THREAD_NUMBERS);

        //2.创建SequenceBarrier
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        /****************** @beg 消费者消费数据  ******************/
        //3.1创建消息处理器
        BatchEventProcessor<Trade> transProcessor = new BatchEventProcessor<Trade>(
                ringBuffer, sequenceBarrier, new TradeHandler());

        //3.2这一步的目的就是把消费者的位置信息引用注入到生产者    如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(transProcessor.getSequence());

        //3.3把消息处理器提交到线程池
        executors.submit(transProcessor);
        /****************** @end 消费者消费数据  ******************/

        //如果存在多个消费者 那重复执行上面3 把TradeHandler换成其它消费者类

        /****************** @beg 生产者生产数据   ******************/
        Future<?> future = executors.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i < 10; i++) {
                    seq = ringBuffer.next();  //占个坑 --ringBuffer一个可用区块
                    ringBuffer.get(seq).setPrice(Math.random() * 9999);  //给这个区块放入 数据
                    ringBuffer.publish(seq);  //发布这个区块的数据使handler(consumer)可见
                }
                return null;
            }
        });
        /****************** @end 生产者生产数据   ******************/


        future.get(); //等待生产者结束
        Thread.sleep(1000);//等上1秒，等消费都处理完成
        transProcessor.halt();//通知事件(或者说消息)处理器 可以结束了（并不是马上结束!!!）
        executors.shutdown();//终止线程
    }

    class Trade {
        double price;
        String id;//ID
        public void setPrice(double price) {
            this.price = price;
        }
        public void setId(String id) {
            this.id = id;
        }
        @Override
        public String toString() {
            return "Trade{" +
                    "price=" + price +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
    class TradeHandler implements EventHandler<Trade>, WorkHandler<Trade> {
        @Override
        public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
            this.onEvent(event);
        }

        @Override
        public void onEvent(Trade event) throws Exception {
            //Do something 消费逻辑
            event.setId(UUID.randomUUID().toString());//简单生成下ID
            System.out.println("Event: " + event);
        }
    }
}
