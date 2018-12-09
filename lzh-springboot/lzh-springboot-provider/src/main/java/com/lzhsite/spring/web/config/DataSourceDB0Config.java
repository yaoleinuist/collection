package com.lzhsite.spring.web.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * Created by Administrator on 2017/3/26/026.
 * 数据源配置
 */
@Configuration
@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.db0*"},sqlSessionTemplateRef = "sqlSessionTemplateDB0") // 扫描dao或mapper接口
//启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
public class DataSourceDB0Config {

    @Primary
    @Bean(name = "dataSourceDB0")
    public DataSource dataSourceDB0(DataSourceDB0Properties dataSourceDB0Properties){
        DruidXADataSource dataSource = new DruidXADataSource();
    	BeanUtils.copyProperties(dataSourceDB0Properties,dataSource);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("dataSourceDB0");
        xaDataSource.setBorrowConnectionTimeout(60);
        xaDataSource.setMaxPoolSize(20);
        xaDataSource.setMinPoolSize(5);
        return dataSource;
    }

    @Primary
    @Bean(name = "sqlSessionFactoryDB0")
    public SqlSessionFactory sqlSessionFactoryDB0(@Qualifier("dataSourceDB0") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.lzhsite.spring.web.entity.db0");
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/lzhsite/spring/web/mapping/message/*.xml"));
        return bean.getObject();
    }
    @Primary
    @Bean(name = "sqlSessionTemplateDB0")
    public SqlSessionTemplate sqlSessionTemplateDB0(
            @Qualifier("sqlSessionFactoryDB0") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
	/**
	 * 注入LCN的代理连接池
	 * 
	 * @return
	 */
	@Bean("transactionManager0")
	public PlatformTransactionManager txManager1(DataSourceDB0Properties dataSourceDB1Properties) {

		return new DataSourceTransactionManager(dataSourceDB0(dataSourceDB1Properties));
	}

 
}
