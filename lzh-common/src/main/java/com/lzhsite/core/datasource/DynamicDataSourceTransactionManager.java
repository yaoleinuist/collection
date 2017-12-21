package com.lzhsite.core.datasource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * 数据源管理
 * Created by pangpeijie on 16/11/11.
 */
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    private static final long serialVersionUID = 1084129294595109594L;

    /**
     * 只读事务到从库
     * 读写事务到主库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        boolean readOnly = definition.isReadOnly();
        if (readOnly) {
            DataSourceHolder.setSlave();
        } else {
            DataSourceHolder.setMaster();
        }
        super.doBegin(transaction, definition);
    }

    /**
     * 清理本地线程的数据源
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DataSourceHolder.clearDataSource();
    }
}
