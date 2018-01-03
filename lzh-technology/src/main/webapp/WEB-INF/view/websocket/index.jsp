<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
		<head>
		<%@ include file="/WEB-INF/view/common/meta.jsp"%>
		<base href="${ctx}">
		<meta name="viewport" content="width=device-width" />
		<meta charset="UTF-8"/>
		<title>WebSocket客户端</title>
	    </head>
		<script>
	    var websocket_url="ws://"+request.getServerName()+":"+request.getServerPort()+base_path+"websocket";
		
		$(function() {
		
		    var websocket;
		    // 首先判断是否 支持 WebSocket
		    if('WebSocket' in window) {
		        websocket = new WebSocket(websocketUrl);
		    } else if('MozWebSocket' in window) {
		        websocket = new MozWebSocket(websocketUrl);
		    } else {
		        websocket = new SockJS(base_basePath+websocket);
		    }
		
		    // 打开时
		    websocket.onopen = function(evnt) {
		        console.log("  websocket.onopen  ");
		    };
		
		
		    // 处理消息时
		    websocket.onmessage = function(evnt) {
		        $("#msg").append("<p>(<font color='red'>" + evnt.data + "</font>)</p>");
		        console.log("  websocket.onmessage   ");
		    };
		
		
		    websocket.onerror = function(evnt) {
		        console.log("  websocket.onerror  ");
		    };
		
		    websocket.onclose = function(evnt) {
		        console.log("  websocket.onclose  ");
		    };
		
		
		    // 点击了发送消息按钮的响应事件
		    $("#TXBTN").click(function(){
		
		        // 获取消息内容
		        var text = $("#tx").val();
		
		        // 判断
		        if(text == null || text == ""){
		            alert(" content  can not empty!!");
		            return false;
		        }
		
		        var msg = {
		            msgContent: text,
		            postsId: 1
		        };
		
		        // 发送消息
		        websocket.send(JSON.stringify(msg));
		
		    });
		
		
		});
		</script>
    <body>

        <!-- 最外边框 -->
        <div style="margin: 20px auto; border: 1px solid blue; width: 300px; height: 500px;">

            <!-- 消息展示框 -->
            <div id="msg" style="width: 100%; height: 70%; border: 1px solid yellow;overflow: auto;"></div>

            <!-- 消息编辑框 -->
            <textarea id="tx" style="width: 100%; height: 20%;"></textarea>

            <!-- 消息发送按钮 -->
            <button id="TXBTN" style="width: 100%; height: 8%;">发送数据</button>

        </div>


    </body>

</html>

