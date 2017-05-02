package com.technology.nio.demo;


import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;


/**
 * 选择器和异步IO：通过选择器来提高多路复用
 * 编译这段代码，然后通过类似于java MultiPortEcho 8005 8006这样的命令来启动它。
 * 一旦这个程序运行成功，启动一个简单的telnet或者其他的终端模拟器来连接8005和8006接口。
 * 你会看到这个程序会回显它接收到的所有字符——并且它是通过一个Java线程来实现的。
 * @author lzh
 *
 */
public class MultiPortEcho{

  private int ports[];

  private ByteBuffer echoBuffer = ByteBuffer.allocate( 1024 );



  public MultiPortEcho( int ports[] ) throws IOException {

    this.ports = ports;



    configure_selector();

  }



  private void configure_selector() throws IOException {

    // Create a new selector

    Selector selector = Selector.open();



    // Open a listener on each port, and register each one

    // with the selector

    for (int i=0; i<ports.length; ++i) {

      ServerSocketChannel ssc = ServerSocketChannel.open();

      ssc.configureBlocking(false);

      ServerSocket ss = ssc.socket();

      InetSocketAddress address = new InetSocketAddress(ports[i]);

      ss.bind(address);



      SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);



      System.out.println("Going to listen on " + ports[i]);

    }



    while (true) {

      int num = selector.select();

      Set selectedKeys = selector.selectedKeys();
      Iterator it = selectedKeys.iterator();



      while (it.hasNext()) {

        SelectionKey key = (SelectionKey) it.next();

        if ((key.readyOps() & SelectionKey.OP_ACCEPT)

          == SelectionKey.OP_ACCEPT) {

          // Accept the new connection

          ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
          SocketChannel sc = ssc.accept();
          sc.configureBlocking(false);



          // Add the new connection to the selector

          SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
          it.remove();
          System.out.println( "Got connection from "+sc );

        } else if ((key.readyOps() & SelectionKey.OP_READ)

          == SelectionKey.OP_READ) {

          // Read the data
          SocketChannel sc = (SocketChannel)key.channel();

          // Echo data
          int bytesEchoed = 0;

          while (true) {

            echoBuffer.clear();
            int number_of_bytes = sc.read(echoBuffer);

            if (number_of_bytes <= 0) {

              break;

            }

            echoBuffer.flip();
            sc.write(echoBuffer);
            bytesEchoed += number_of_bytes;

          }

          System.out.println("Echoed " + bytesEchoed + " from " + sc);
          it.remove();

        }


      }

    }

  }



  static public void main( String args[] ) throws Exception {

    if (args.length<=0) {

      System.err.println("Usage: java MultiPortEcho port [port port ...]");

      System.exit(1);

    }



    int ports[] = new int[args.length];



    for (int i=0; i<args.length; ++i) {

      ports[i] = Integer.parseInt(args[i]);

    }



    new MultiPortEcho(ports);

  }

}