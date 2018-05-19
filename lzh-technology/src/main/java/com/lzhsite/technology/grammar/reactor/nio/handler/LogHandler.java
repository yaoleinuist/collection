package com.lzhsite.technology.grammar.reactor.nio.handler;

import java.util.Date;

import com.lzhsite.technology.grammar.reactor.nio.Request;
import com.lzhsite.technology.grammar.reactor.nio.event.EventAdapter;

/**
 * 日志记录
 */
public class LogHandler extends EventAdapter {
    public LogHandler() {
    }
    
    public void onAccepted(Request request) throws Exception{
    	System.out.println("LogHandler: onAccepted event is active !");
    }

    public void onClosed(Request request) throws Exception {
        String log = new Date().toString() + " from " + request.getAddress().toString();
        System.out.println(log);
    }

    public void onError(String error) {
        System.out.println("Error: " + error);
    }
}
