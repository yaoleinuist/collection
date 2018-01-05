package com.lzhsite.technology.concurrent.producerAndCustomer;

import com.lzhsite.technology.concurrent.TestSynchronized;

/**
 * Java多线程之简单生产者和消费者例子 
 * @author lzh
 *
 */
public class ProducerAndCustomer {
	
	private int m_iData = 0;  
    private boolean m_bSetData = true;  
    public synchronized void producerData(int iInputData) {// 添加synchronized保证线程同步功能  
        if (m_bSetData) {  
            try {  
                this.wait(); //等待消费者消费  
            } catch (InterruptedException e) {  
                System.out.print("InterrupExcepthion put data\n");  
            }  
        }  
        System.out.print("producer: " + iInputData);  
        m_iData = iInputData;  
        m_bSetData = true;  
        this.notify(); //继续生产  
  
    }  
      
    public synchronized void consumerData() {  
        if (!m_bSetData) {  
            try {  
                this.wait(); //等待生产者产生数据，停止消费  
            } catch (InterruptedException e) {  
                System.out.print("InterrupExcepthion get data\n");  
            }  
        }  
        System.out.print("consumer: " + m_iData + "\n");  
        m_bSetData = false;  
        this.notify(); //唤醒消费者线程，继续消费。  
    }  
    
    public static void main(String[] args) {
		
    	
    	ProducerAndCustomer producerAndCustomer	= new ProducerAndCustomer();
    	
        Thread consumer = new Thread(new Consumer(producerAndCustomer));
        Thread producer = new Thread(new Producer(producerAndCustomer));
 
        consumer.start();
        producer.start();
    	
	}
}

class Consumer implements Runnable{  
    private ProducerAndCustomer m_data;  
    public Consumer(ProducerAndCustomer d) {  
        m_data = d;  
    }  
      
    public void run() {  
        System.out.print("consumer thread run");  
        while (true) {  
            m_data.consumerData();  
        }  
    }  
} 

class Producer implements Runnable{  
    private ProducerAndCustomer m_data;  
    public Producer(ProducerAndCustomer d) {  
        m_data = d;  
    }  
      
    public void run() {  
        System.out.print("producer thread run");  
        int  inPutData = 0;  
        while (true) {  
            m_data.producerData(inPutData++);  
        }  
    }  
  
}  


