package com.technology.thread.threadPool.MythreadPool;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.technology.thread.threadPool.MythreadPool.inter.ITask;
import com.technology.thread.threadPool.MythreadPool.util.DateUtil;
import com.technology.thread.threadPool.MythreadPool.util.ThreadPoolException;

/**  
 *   
 * MyThreadPool  
 * 线程池 
 * @author yangchuang  
 * @since 2014-3-15 上午09:45:39    
 * @version 1.0.0  
 * 修改记录
 * 修改日期    修改人    修改后版本    修改内容
 */
public class MyThreadPool {
    /**  
     * maxNumThreadSize:最多的线程数量
     * @since 1.0.0  
     */  
    private int maxNumThreadSize;
    
    /**  
     * minNumThreadSize:最少的线程数量
     * @since 1.0.0  
     */  
    private int minNumThreadSize;
    
    /**  
     * keepAliveTime:线程池维护线程所允许的空闲时间 单位（秒）
     * @since 1.0.0  
     */  
    private int keepAliveTime;
    
    /**  
     * taskPool:任务池
     * 按照优先级来进行存储
     * @since 1.0.0  
     */  
    private TaskPool<ITask> taskPool=new TaskPool<ITask>();
    
    /**  
     * workThreadQuery:线程安全的线程队列
     */  
    private BlockingQueue<WorkThread> workThreadQuery=new LinkedBlockingQueue<WorkThread>();
    
    /**  
     * EXECUTE_TASK_LOCK:执行任务队列的时候，wait()与notiyfAll()的监视对象
     * @since 1.0.0  
     */  
    private static final String EXECUTE_TASK_LOCK=new String("executeTaskLock");
    
    /**  
     * threadFreeSet:空闲线程的ID存放在此
     * @since 1.0.0  
     */  
    private ConcurrentMap<String,String> threadFreeMap=new ConcurrentHashMap<String,String>();
    
    /**  
     * maintainTimer:定时维护线程池的
     * @since 1.0.0  
     */  
    private Timer maintainTimer;
    
      
    /**  
     * 创建一个新的实例 MyThreadPool.  
     *      最多的线程数量=7
     *      最少的线程数量=3
     *      线程池维护线程所允许的空闲时间 单位（秒）=30（秒）
     */
    public MyThreadPool(){
        this.maxNumThreadSize=7;
        this.minNumThreadSize=3;
        this.keepAliveTime=30*DateUtil.DATE_SECOND;
        loadWorkThreadQuery();
        maintainTimer=new Timer();
    }
    
      
    /**  
     * 创建一个新的实例 MyThreadPool.  
     *  
     * @param maxNumThreadSize        最多的线程数量
     * @param minNumThreadSize        最少的线程数量
     * @param keepAliveTime         线程池维护线程所允许的空闲时间 单位（秒）
     * @throws ThreadPoolException  
     */
    public MyThreadPool(int maxNumThreadSize,int minNumThreadSize,int keepAliveTime) throws ThreadPoolException{
        if(minNumThreadSize<1){
            throw new ThreadPoolException("线程池中不能没有线程");
        }
        if(maxNumThreadSize<minNumThreadSize){
            throw new ThreadPoolException("最多的线程数不能少于最少的线程数");
        }
        if(keepAliveTime<1){
            throw new ThreadPoolException("线程池维护线程所允许的空闲时间不能少于1秒");
        }
        this.maxNumThreadSize=maxNumThreadSize;
        this.minNumThreadSize=minNumThreadSize;
        this.keepAliveTime=keepAliveTime*DateUtil.DATE_SECOND;
        loadWorkThreadQuery();
        maintainTimer=new Timer();
    }
    
    private void loadWorkThreadQuery(){
        for(int i=0;i<minNumThreadSize;i++){
            new WorkThread();
        }
    }
    
    private class WorkThread extends Thread{
        private String ID;
        private boolean isWorking;
        private boolean isDied;
        public WorkThread(){
            ID=""+hashCode();
            workThreadQuery.add(this);
            addThreadFreeNum(this.getID());
            System.out.println("新线程被添加++++空闲线程数"+threadFreeMap.size()+"  总的线程数"+workThreadQuery.size()+"  未处理的任务数："+taskPool.taskSize());
        }
        @Override
        public void run() {
            while(!isDied){
                while(taskPool.taskSize()<1&&!isDied){
                    System.out.println(Thread.currentThread().getName()+"  无任务！睡眠");
                    setStopWorking();
                    try {
                        synchronized(EXECUTE_TASK_LOCK){
                            EXECUTE_TASK_LOCK.wait();
                            System.out.println(Thread.currentThread().getName()+"  被唤醒");
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while(taskPool.taskSize()>0&&!isDied){
                    System.out.println(Thread.currentThread().getName()+"  有任务执行;任务总数："+taskPool.taskSize());
                    setStartWorking();
                    ITask task = taskPool.removeTask();
                    try {
                        if(task!=null){
                            task.stratWork();
                        }
                    }
                    catch (Exception e) {
                        try {
                            taskPool.rejoinTask(task);
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(Thread.currentThread().getName()+"  死亡");
        }
        public void setStartWorking() {
            this.isWorking = true;
            subtractThreadFreeNum(this.getID());
        }
        public void setStopWorking() {
            this.isWorking = false;
            addThreadFreeNum(this.getID());
        }
        public void setDied(boolean isDied) {
            this.isDied = isDied;
            workThreadQuery.remove(this);
            subtractThreadFreeNum(this.getID());
            synchronized(EXECUTE_TASK_LOCK){
                EXECUTE_TASK_LOCK.notifyAll();
            }
        }
        public String getID() {
            return ID;
        }
        public void setID(String iD) {
            ID = iD;
        }
        
    }
    
    public void execute(){
        for(WorkThread workThread:workThreadQuery){
            workThread.start();
        }
        /**
         * 调用 schedule() 方法后，要等待3s才可以第一次执行 run() 方法
         * 每隔keepAliveTime调用一次
         */
        maintainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                maintainPool();
            }
        }, 3000, keepAliveTime);
    }
    
    private void maintainPool(){
        System.out.println("开始维护线程池++++++++++++++++");
        System.out.println(this);
        // 任务数量大于空闲线程数量，并且总线程数量小于线程池的规定的最大值，就新增线程
        if(taskPool.taskSize()>this.threadFreeMap.size()&&workThreadQuery.size()<maxNumThreadSize){
            // 最多再允许创建的线程数
            int maxCreatThreadNum=maxNumThreadSize-workThreadQuery.size();
            // 需要创建的线程数
            int needCreatThreadNum=taskPool.taskSize()-this.threadFreeMap.size();
            // 实际的创建数目
            int creatThreadNum=needCreatThreadNum>maxCreatThreadNum?maxCreatThreadNum:needCreatThreadNum;
            for(int i=0;i<creatThreadNum;i++){
                WorkThread workThread=new WorkThread();
                workThread.start();
            }
        }
        // 空闲线程多于任务队列的数量，并且总的线程多余最小的线程个数，就要减少线程
        if(taskPool.taskSize()<this.threadFreeMap.size()&&workThreadQuery.size()>minNumThreadSize){
            // 允许销毁的最大线程数
            int maxDestroyThreadNum=workThreadQuery.size()-minNumThreadSize;
            // 需要销毁的线程数
            int needDestroyThreadNum=this.threadFreeMap.size()-taskPool.taskSize();
            // 实际的销毁数目
            int destroyThreadNum=needDestroyThreadNum>maxDestroyThreadNum?maxDestroyThreadNum:needDestroyThreadNum;
            for(int i=0;i<destroyThreadNum;i++){
                for(WorkThread workThread:workThreadQuery){
                    if(!workThread.isWorking){
                        workThread.setDied(true);
                        break;
                    }
                }
            }
        }
        System.out.println("维护后的信息");
        System.out.println(this);
        System.out.println("结束维护线程池++++++++++++++++");
    }
    
    public void addTask(ITask task) throws Exception{
        taskPool.addTask(task);
        synchronized(EXECUTE_TASK_LOCK){
            EXECUTE_TASK_LOCK.notifyAll();
        }
    }
    
    public int getMaxNumThreadSize() {
        return maxNumThreadSize;
    }
    public void setMaxNumThreadSize(int maxNumThreadSize) {
        this.maxNumThreadSize = maxNumThreadSize;
    }
    public int getMinNumThreadSize() {
        return minNumThreadSize;
    }
    public void setMinNumThreadSize(int minNumThreadSize) {
        this.minNumThreadSize = minNumThreadSize;
    }
    public int getKeepAliveTime() {
        return keepAliveTime;
    }
    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public synchronized void addThreadFreeNum(String id) {
        this.threadFreeMap.put(id,id);
    }
    public synchronized void subtractThreadFreeNum(String id) {
        this.threadFreeMap.remove(id,id);
    }
    @Override
    public String toString(){
        String showInfo="%%%%%%%%空闲的线程数"+threadFreeMap.size()+"  总的线程数"+workThreadQuery.size()+"  未处理的任务数："+taskPool.taskSize();
        showInfo+="\n%%%%%%%%threadFreeSet:"+threadFreeMap;
        showInfo+="\n%%%%%%%%workThreadQuery:"+workThreadQuery;
        return showInfo;
    }
}
