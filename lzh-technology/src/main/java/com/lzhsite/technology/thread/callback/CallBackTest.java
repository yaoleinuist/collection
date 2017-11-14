package com.lzhsite.technology.thread.callback;

public class CallBackTest {
	public static void main(String[] args) {
		Server server = new Server();
		Client client = new Client(server);

		client.sendMsg("Server,Hello~");
	}
}

class Server {
	public void getClientMsg(CSCallBack csCallBack, String msg) {
		System.out.println("服务端：服务端接收到客户端发送的消息为:" + msg);

		// 模拟服务端需要对数据处理
		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("服务端:数据处理成功，返回成功状态 200");
		String status = "200";
		csCallBack.process(status);
	}
}

class Client implements CSCallBack {

	private Server server;

	public Client(Server server) {
		this.server = server;
	}

	public void sendMsg(final String msg) {
		System.out.println("客户端：发送的消息为：" + msg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				server.getClientMsg(Client.this, msg);
			}
		}).start();
		System.out.println("客户端：异步发送成功");
	}

	@Override
	public void process(String status) {
		System.out.println("客户端：服务端回调状态为：" + status);
	}

}

 