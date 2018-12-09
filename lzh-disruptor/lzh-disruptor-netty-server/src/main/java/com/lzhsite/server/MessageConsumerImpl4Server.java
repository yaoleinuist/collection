package com.lzhsite.server;

import com.lzhsite.disruptor.MessageConsumer;
import com.lzhsite.entity.TranslatorData;
import com.lzhsite.entity.TranslatorDataWapper;

import io.netty.channel.ChannelHandlerContext;

public class MessageConsumerImpl4Server extends MessageConsumer {

	public MessageConsumerImpl4Server(String consumerId) {
		super(consumerId);
	}

	public void onEvent(TranslatorDataWapper event) throws Exception {
		TranslatorData request = event.getData();
		ChannelHandlerContext ctx = event.getCtx();
		//1.业务处理逻辑:
    	System.err.println("Sever端: id= " + request.getId() 
		+ ", name= " + request.getName() 
		+ ", message= " + request.getMessage());
    	
    	//2.回送响应信息:
    	TranslatorData response = new TranslatorData();
    	response.setId("resp: " + request.getId());
    	response.setName("resp: " + request.getName());
    	response.setMessage("resp: " + request.getMessage());
    	//写出response响应信息:
    	ctx.writeAndFlush(response);
	}

}
