package com.lzhsite.technology.designPattern.observer;
/**
 * 观察者模式：
 * 观察者(Observer)相当于事件监听者，被观察者(Observable)相当于事件源和事件，
 * 执行逻辑时通知observer即可触发oberver的update,同时可传被观察者和参数
 * @author lzh
 *
 */
public class TestObserver implements java.util.Observer {     
	
	  public void update(java.util.Observable obj, Object arg) {     
		    System.out.println("Update() called, count is "      
		                                + ((Integer) arg).intValue());     
		}   
	  
	  public static void main(String[] args) {     
	        BeingWatched beingWatched = new BeingWatched();//受查者     
	        Watcher watcher = new Watcher();//观察者     
	        beingWatched.addObserver(watcher);     
	        beingWatched.counter(10);     
	    }  
	  
} 

class Watcher  implements java.util.Observer {     
	
	  public void update(java.util.Observable obj, Object arg) {     
		    System.out.println("Update() called, count is "      
		                                + ((Integer) arg).intValue());     
		}   
}

class BeingWatched extends java.util.Observable {     
    void counter(int period) {     
        for(; period>=0; period-- ) {     
                setChanged();     
                notifyObservers(new Integer(period));     
                try {     
                        Thread.sleep(100);     
                } catch( InterruptedException e) {     
                  System.out.println("Sleep interrupeted" );     
                }     
        }     
}     
};  