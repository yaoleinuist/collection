package com.lzhsite.spring.web.config;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;


/**
 * @Project: design-pattern
 * @Package: com.lcj.config
 * @Author: Administrator
 * @Description: TODO
 * @Version: 1.0
 * @Datetime: 2017/9/27 17:07
 */
@Configuration
public class XATransactionManagerConfig {

//    @Bean(name = "userTransaction")
//    public UserTransaction userTransaction() throws Throwable {
//        UserTransactionImp userTransactionImp = new UserTransactionImp();
//        userTransactionImp.setTransactionTimeout(10000);
//        return userTransactionImp;
//    }
//
//    @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
//    public TransactionManager atomikosTransactionManager() throws Throwable {
//        UserTransactionManager userTransactionManager = new UserTransactionManager();
//        userTransactionManager.setForceShutdown(false);
//        return userTransactionManager;
//    }
// 
//	/**
//	 * 注入LCN的代理连接池
//	 * 
//	 * @return
//	 */
//	@Bean(name = "transactionManager")
//	@DependsOn({ "userTransaction", "atomikosTransactionManager" })
//	public PlatformTransactionManager transactionManager() throws Throwable {
//		PlatformTransactionManager platformTransactionManager=new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
//		return   platformTransactionManager;
//	}


}
