package com.lzhsite.webkits.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
 

//https://github.com/waylau/netty-4-user-guide-demos/tree/master/netty4-demos/src/main/java/com/waylau/netty
//http://www.importnew.com/21561.html
//https://www.cnblogs.com/nosqlcoco/p/5860730.html
//http://blog.csdn.net/mybook201314/article/details/70173674

public class MyWebSocketHandler implements WebSocketHandler{


    private static final Logger log = Logger.getLogger(MyWebSocketHandler.class);

    // 保存所有的用户session

	private static final ArrayList<WebSocketSession> users;
	static{
		users = new ArrayList<WebSocketSession>();
	}


    // 连接 就绪时 
    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {

        log.info("connect websocket success.......");

        users.add(session);

    }


    // 处理信息
    @Override
    public void handleMessage(WebSocketSession session,
            WebSocketMessage<?> message) throws Exception {
    	
        Gson gson = new Gson();

        // 将消息JSON格式通过Gson转换成Map
        // message.getPayload().toString() 获取消息具体内容
        Map<String, Object> msg = gson.fromJson(message.getPayload().toString(), 
                new TypeToken<Map<String, Object>>() {}.getType());

        log.info("handleMessage......."+message.getPayload()+"..........."+msg);

//      session.sendMessage(message);

        // 处理消息 msgContent消息内容
        TextMessage textMessage = new TextMessage(msg.get("msgContent").toString(), true);
        // 调用方法（给所有在线用户发送消息）
        sendMessageToUsers(textMessage);
 
    }


    // 处理传输时异常
    @Override
    public void handleTransportError(WebSocketSession session,
            Throwable exception) throws Exception {
        // TODO Auto-generated method stub
		if (session.isOpen()) {
			session.close();
		}
		users.remove(session);
    }



    // 关闭 连接时
    @Override
    public void afterConnectionClosed(WebSocketSession session,
            CloseStatus closeStatus) throws Exception {

        log.info("connect websocket closed.......");

        users.remove(session);

    }



    @Override
    public boolean supportsPartialMessages() {
        // TODO Auto-generated method stub
        return false;
    }

	/**
	 * 给所有在线用户发送消息
	 * @param message
	 */
	public void sendMessageToUsers(TextMessage message) {
		for (WebSocketSession user : users) {
			if (user.isOpen()) {
				try {
					user.sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

  
	public void sendMessageToUser(String username,TextMessage message) {
		for (WebSocketSession user : users) {
			if (user.getAttributes().get("username").equals(username)) {
				try {
					if (user.isOpen()) {
						user.sendMessage(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

}