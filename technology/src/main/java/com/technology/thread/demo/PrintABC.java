package com.technology.thread.demo;

public class PrintABC {  
	  
    public static Boolean isThreadA = true;  
    public static Boolean isThreadB = false;  
    public static Boolean isThreadC = false;  
  
    public static void main(String[] args) {  
        final PrintABC abc = new PrintABC();  
        new Thread(new Runnable() {  
            public void run() {  
                for (int i = 0; i < 10; i++) {  
                    synchronized (abc) {  
                        while(!isThreadA) {  
                            try {  
                                abc.wait();  
                            } catch (InterruptedException e) {  
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                            }  
                        }  
                            System.out.print("A");  
                            isThreadA = false;  
                            isThreadB = true;  
                            isThreadC = false;  
                            abc.notifyAll();  
                    }  
                }  
            }  
        }).start();  
  
        new Thread(new Runnable() {  
            public void run() {  
                for (int i = 0; i < 10; i++) {  
                    synchronized (abc) {  
                        while(!isThreadB) {  
                            try {  
                                abc.wait();  
                            } catch (InterruptedException e) {  
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                            }  
                        }  
                            System.out.print("B");  
                            isThreadA = false;  
                            isThreadB = false;  
                            isThreadC = true;  
                            abc.notifyAll();  
                    }  
                }  
            }  
        }).start();  
          
        new Thread(new Runnable() {  
            public void run() {  
                for (int i = 0; i < 10; i++) {  
                    synchronized (abc) {  
                        while(!isThreadC) {  
                            try {  
                                abc.wait();  
                            } catch (InterruptedException e) {  
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                            }  
                        }  
                            System.out.print("C");  
                            isThreadA = true;  
                            isThreadB = false;  
                            isThreadC = false;  
                            abc.notifyAll();  
                    }  
                }  
            }  
        }).start();  
    }  
}
