package com.lzhsite.spring.web.config;


import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.codingapi.tx.config.service.TxManagerTxUrlService;

@Configuration
@ComponentScan(basePackages={"com.codingapi.tx.*","com.lzhsite.spring.*"})
//启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
//@ImportResource(locations = {"classpath*:dubbo_provider.xml"})
//@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.db0*"},sqlSessionTemplateRef = "sqlSessionTemplateDB0") // 扫描dao或mapper接口
@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.db0*"},sqlSessionFactoryRef="sqlSessionFactoryDB0") 
public class LCNConfig implements TxManagerTxUrlService{


    @Value("${tm.manager.url}")
    private String url;

    @Override
    public String getTxUrl() {
        System.out.println("load tm.manager.url ");
        return url;
    }
    @Primary
    @Bean(name = "dataSourceDB0")
    public DataSource dataSourceDB0(DataSourceDB0Properties dataSourceDB1Properties){
        //DruidXADataSource dataSource = new DruidXADataSource();
        DruidDataSource dataSource = new DruidDataSource();
    	BeanUtils.copyProperties(dataSourceDB1Properties,dataSource);
//        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
//        xaDataSource.setXaDataSource(dataSource);
//        xaDataSource.setUniqueResourceName("dataSourceDB0");
        return dataSource;
    }
    

    /**
     * 注入LCN的代理连接池
     * @return
     */
    @Bean("transactionManager")
    public PlatformTransactionManager txManager1(DataSourceDB0Properties dataSourceDB1Properties){
      
    	return new DataSourceTransactionManager(dataSourceDB0(dataSourceDB1Properties));
    }

    @Bean(name = "sqlSessionFactoryDB0")
    public SqlSessionFactory sqlSessionFactoryDB0(DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.lzhsite.spring.web.entity.db0");
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/lzhsite/spring/web/mapping/*.xml"));
        return bean.getObject();
    }
//
//    @Bean(name = "sqlSessionTemplateDB0")
//    public SqlSessionTemplate sqlSessionTemplateDB0(
//            @Qualifier("sqlSessionFactoryDB0") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//    

   
}
