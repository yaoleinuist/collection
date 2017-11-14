package com.lzhsite.es.demo.util;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class MyTransactionManager extends JpaTransactionManager {

    /**
     * 
     */
    private static final long serialVersionUID = -3878501009638970644L;

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        if(!definition.isReadOnly()){ //只读事务无需操作索引
            ElasticsearchTransactionUtil.init();
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        super.doCommit(status);
        if(!status.isReadOnly()){ //只读事务无需操作索引
            ElasticsearchTransactionUtil.commit();
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        if(!status.isReadOnly()){ //只读事务无需操作索引
            ElasticsearchTransactionUtil.rollback();
        }
    }
}
