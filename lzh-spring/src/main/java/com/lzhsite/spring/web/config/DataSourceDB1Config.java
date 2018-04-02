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

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * Created by Administrator on 2017/3/26/026.
 * 数据源配置s
 */
@Configuration
@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.db1*"}, sqlSessionTemplateRef = "sqlSessionTemplateDB1") // 扫描dao或mapper接口
public class DataSourceDB1Config {


    @Bean(name = "dataSourceDB1")
    public DataSource dataSourceDB2(DataSourceDB1Properties dataSourceDB2Properties){
        DruidXADataSource dataSource = new DruidXADataSource();
        BeanUtils.copyProperties(dataSourceDB2Properties,dataSource);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("dataSourceDB1");
        return xaDataSource;
    }

    @Bean(name = "sqlSessionFactoryDB1")
    public SqlSessionFactory sqlSessionFactoryDB2(@Qualifier("dataSourceDB1") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.lzhsite.spring.web.entity.db1");
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/DB1/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplateDB1")
    public SqlSessionTemplate sqlSessionTemplateDB1(
            @Qualifier("sqlSessionFactoryDB1") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
