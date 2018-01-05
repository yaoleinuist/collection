package com.lzhsite.technology.concurrent.threadPool.MythreadPool.util;

/**  
 *   
 * ThreadPoolException  
 * 线程池的自定义异常 
 * @author yangchuang  
 * @since 2014-3-15 上午10:13:05    
 * @version 1.0.0  
 * 修改记录
 * 修改日期    修改人    修改后版本    修改内容
 */
public class ThreadPoolException extends Exception {
    public ThreadPoolException(){
        super();
    }
    public ThreadPoolException(String message){
        super(message);
    }
    public ThreadPoolException(String message,Throwable cause){
        super(message,cause);
    }
    public ThreadPoolException(Throwable cause){
        super(cause);
    }
}
