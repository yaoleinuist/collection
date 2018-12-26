package com.lzhsite.server;

import com.lzhsite.disruptor.MessageProducer;
import com.lzhsite.disruptor.RingBufferWorkerPoolFactory;
import com.lzhsite.entity.TranslatorData;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	/**
    	 * 服务端接收客户端发送的消息
    	TranslatorData request = (TranslatorData)msg;
    	System.err.println("Sever端: id= " + request.getId() 
    					+ ", name= " + request.getName() 
    					+ ", message= " + request.getMessage());
    	//数据库持久化操作 IO读写 ---> 交给一个线程池 去异步的调用执行
    	TranslatorData response = new TranslatorData();
    	response.setId("resp: " + request.getId());
    	response.setName("resp: " + request.getName());
    	response.setMessage("resp: " + request.getMessage());
    	//写出response响应信息:
    	//下面的操作包含释放了msg
    	ctx.writeAndFlush(response);
    	*/
    	TranslatorData request = (TranslatorData)msg;
    	//自已的应用服务应该有一个ID生成规则
    	String producerId = "code:sessionId:001";
    	MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
    	messageProducer.onData(request, ctx);
    	
    	
    }
    
}
