  1.对于Java Socket编程而言，有两个概念，一个是ServerSocket，一个是Socket。服务端和客户端之间通过Socket建立连接， 
    之后它们就可以进行通信了。 首先ServerSocket将在服务端监听某个端口，当发现客户端有Socket来试图连接它时，
    它会accept该Socket的连接请求，同时在服务端建立一个对应的 Socket与之进行通信。这样就有两个Socket了，客户端和服务端各一个。
  
    参考：http://blog.csdn.net/zhangyiacm/article/details/49488721
    
  2. Freemarker.它是为程序显示数据而准备的.(像数据库SQL语句的查询.)以及.
     Freemarker仅仅是利用模板加上数据生成文本页面，Freemarker并不是一个WEB应用程序框架.可以说是一生成文本而已.
         注意,它是MVC框架的一个组件(如STRUTS),也可以在模板中使用JSP标签.  
    
  3.NIO也提供了SocketChannel和ServerSocketChannel两种不同的套接字通道来实现,为了实现高负载高并发都采取非阻塞的模式,服务器
      仅采用一个一个线程去处理所有客户端线程， 这就需要创建一个selector,selector就好像是一个观察者，通过不断轮询 待的操作发生，
      当等待事件发 生的时候可以做其他事情，当有信道出现感兴趣的操作，则该信道就进入就绪状态。Slector的select方法阻塞等待又没有
      就绪的通道, 当出现就绪的信道或者等待超时返回，就绪信道的个数，若等待超时则返回-1，selectedKeys方法返回就绪的信道。

   
    参考：http://www.2cto.com/kf/201505/402803.html