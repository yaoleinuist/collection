package com.lzhsite.disruptor.heigh.tradeTransaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
/**
 * 使用WorkerPool构建多消费者
 * @author lzhcode
 *
 */
public class Main2 {
	public static void main(String[] args) throws InterruptedException {  
		
        int BUFFER_SIZE=1024;  
        int THREAD_NUMBERS=4;  
        EventFactory<TradeTransactionEvent> eventFactory=new EventFactory<TradeTransactionEvent>() {  
            public TradeTransactionEvent newInstance() {  
                return new TradeTransactionEvent();  
            }  
        };  
        RingBuffer<TradeTransactionEvent> ringBuffer=RingBuffer.createSingleProducer(eventFactory, BUFFER_SIZE);  
          
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();  
          
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBERS);  
          
        WorkHandler<TradeTransactionEvent> workHandlers=new TradeTransactionInDBHandler();  
       
        WorkerPool<TradeTransactionEvent> workerPool=new WorkerPool<TradeTransactionEvent>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), workHandlers);  
          
        workerPool.start(executor);  
          
        //下面这个生产8个数据，图简单就写到主线程算了  
        for(int i=0;i<8;i++){  
            long seq=ringBuffer.next();  
            ringBuffer.get(seq).setPrice(Math.random()*9999);  
            ringBuffer.publish(seq);  
        }  
          
        Thread.sleep(1000);  
        workerPool.halt();  
        executor.shutdown();  
    }  
}
