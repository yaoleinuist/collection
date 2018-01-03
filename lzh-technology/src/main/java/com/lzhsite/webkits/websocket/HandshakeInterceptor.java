package com.lzhsite.webkits.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
/**
 * 这个的主要作用是可以在握手前做一些事，把所需要的东西放入到attributes里面，
 * 然后可以在WebSocketHandler的session中， 取到相应的值，
 * 具体可参考HttpSessionHandshakeInterceptor，这儿也可以实现HandshakeInterceptor 接口。
 * @author lzhcode
 *
 */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor{


    // 握手前
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        System.out.println("++++++++++++++++ HandshakeInterceptor: beforeHandshake  ++++++++++++++"+attributes);

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }



    // 握手后
    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {


        System.out.println("++++++++++++++++ HandshakeInterceptor: afterHandshake  ++++++++++++++");


        super.afterHandshake(request, response, wsHandler, ex);
    }

}
