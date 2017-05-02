package com.technology.thread.threadPool.MythreadPool.inter;

/**  
 *   
 * ITask 
 * 被执行的任务的接口
 * @author yangchuang  
 * @since 2014-3-15 上午09:41:56    
 * @version 1.0.0  
 * 修改记录
 * 修改日期    修改人    修改后版本    修改内容
 */
public interface ITask {
    /**  
     * MAX_PRIORITY:最高优先级
     * @since 1.0.0  
     */  
    public static final int MAX_PRIORITY = 10;
    /**  
     * NORM_PRIORITY:默认优先级
     *  
     * @since 1.0.0  
     */  
    public static final int NORM_PRIORITY  = 5;
    /**  
     * MIN_PRIORITY:最低优先级
     * @since 1.0.0  
     */  
    public static final int MIN_PRIORITY  = 1;
    /**  
     * stratWork    执行具体的逻辑方法  
     * @throws Exception
     * @since  1.0.0  
    */
    public abstract void stratWork() throws Exception;
    /**  
     * getPriority  返回优先级
     *      MAX_PRIORITY:最高优先级
     *      NORM_PRIORITY:默认优先级
     *      MIN_PRIORITY:最低优先级
     * @return
     * @throws Exception
     * @since  1.0.0  
    */
    public abstract int getPriority() throws Exception;
    /**  
     * getMaxAgainExecuteNum    允许再次执行的次数，任务执行失败后，可以再次执行不多于此的次数
     * @return
     * @since  1.0.0  
    */
    public abstract int getMaxAgainExecuteNum();
}
