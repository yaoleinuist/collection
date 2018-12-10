package com.lzhsite.disruptor.quickstart.skill;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class Main {
	 public static void main(String[] args) {
	        producerWithTranslator();
	    }
	    public static void producerWithTranslator(){
	        SeckillEventFactory factory = new SeckillEventFactory();
	        int ringBufferSize = 1024;
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
	        //创建disruptor
	        Disruptor<SeckillEvent> disruptor = new Disruptor<SeckillEvent>(factory, ringBufferSize, executor);
	        //连接消费事件方法
	        disruptor.handleEventsWith(new SeckillEventHandler());
	        //启动
	        disruptor.start();
	        RingBuffer<SeckillEvent> ringBuffer = disruptor.getRingBuffer();
	        SeckillEventProducer producer = new SeckillEventProducer(ringBuffer);
	        for(long i = 0; i<10; i++){
	            producer.seckill(i, i);
	        }
	        disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
	    }
}
