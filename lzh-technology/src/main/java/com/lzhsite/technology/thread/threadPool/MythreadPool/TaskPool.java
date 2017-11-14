package com.lzhsite.technology.thread.threadPool.MythreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.lzhsite.technology.thread.threadPool.MythreadPool.inter.ITask;
import com.lzhsite.technology.thread.threadPool.MythreadPool.util.ThreadPoolException;

/**  
 *   
 * TaskPool  
 * 任务池
 * @author yangchuang  
 * @since 2014-3-15 上午09:53:55    
 * @version 1.0.0  
 * 修改记录
 * 修改日期    修改人    修改后版本    修改内容
 */
public class TaskPool<T extends ITask> {
    /**  
     * taskMap:线程安全的任务Map集合
     * 按照优先级来进行存储
     * @since 1.0.0  
     */  
    private ConcurrentMap<Integer,BlockingQueue<T>> taskMap;
    
    /**  
     * rejoinTaskMap:重发记录表，任务执行失败后，再次加入任务队列的时候，出现在这里记录
     * @since 1.0.0  
     */  
    private ConcurrentMap<T,Integer> rejoinTaskMap;
      
    /**  
     * 创建一个新的实例 TaskPool.  
     */
    public TaskPool(){
        this.taskMap=new ConcurrentHashMap<Integer,BlockingQueue<T>>();
        this.taskMap.put(ITask.MAX_PRIORITY, new LinkedBlockingQueue<T>());
        this.taskMap.put(ITask.NORM_PRIORITY, new LinkedBlockingQueue<T>());
        this.taskMap.put(ITask.MIN_PRIORITY, new LinkedBlockingQueue<T>());
        this.rejoinTaskMap=new ConcurrentHashMap<T,Integer>();
    }
    
    /**  
     * taskSize 获取当前任务池中的任务数量
     * @return  当前任务池中的任务数量
     * @since  1.0.0  
    */
    public int taskSize(){
        return this.taskMap.get(T.MAX_PRIORITY).size()
                +this.taskMap.get(T.NORM_PRIORITY).size()
                +this.taskMap.get(T.MIN_PRIORITY).size();
    }
    
    /**  
     * addTask      添加任务
     * 任务必须设置优先级，并且优先级只能在ITask接口内的三个优先级的值之一
     *      MAX_PRIORITY:最高优先级
     *      NORM_PRIORITY:默认优先级
     *      MIN_PRIORITY:最低优先级
     * @param task  
     * @throws Exception 当该task任务的优先级不在规定的范围，抛出此异常
     * @since  1.0.0  
    */
    public void addTask(T task) throws Exception{
        if(this.taskMap.containsKey(task.getPriority())){
            this.taskMap.get(task.getPriority()).add(task);
            return;
        }
        throw new ThreadPoolException("优先级的值设置有误");
    }
    
    /**  
     * romoveTask      获取任务从优先级高的开始
     * @param task  
     * @throws Exception 
     * @since  1.0.0  
    */
    public synchronized ITask removeTask(){
        if(this.taskMap.get(T.MAX_PRIORITY).size()>0){
            return this.taskMap.get(T.MAX_PRIORITY).remove();
        }
        if(this.taskMap.get(T.NORM_PRIORITY).size()>0){
            return this.taskMap.get(T.NORM_PRIORITY).remove();
        }
        if(this.taskMap.get(T.MIN_PRIORITY).size()>0){
            return this.taskMap.get(T.MIN_PRIORITY).remove();
        }
        return null;
    }
    
    /**  
     * rejoin   当任务执行失败后，再次将他加入任务池
     * @param task
     * @throws Exception 
     * @since  1.0.0  
    */
    public synchronized void rejoinTask(T task) throws Exception{
        // 如果再次执行的Map集合没有该任务那么就添加进去
        if(!rejoinTaskMap.containsKey(task)){
            rejoinTaskMap.put(task, 1);
        }
        // 如果该任务的再次执行的次数大于他设置的值，那么就将其移除出再次执行的Map集合
        if(rejoinTaskMap.get(task)>task.getMaxAgainExecuteNum()){
            rejoinTaskMap.remove(task);
            return;
        }
        // 否则的话，将其加一，再放入任务队列
        else{
            rejoinTaskMap.put(task, rejoinTaskMap.get(task)+1);
        }
        this.taskMap.get(task.getPriority()).add(task);
    }
    
}
