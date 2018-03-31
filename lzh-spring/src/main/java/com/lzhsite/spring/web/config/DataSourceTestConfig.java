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
@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.test*"}, sqlSessionTemplateRef = "sqlSessionTemplateTest") // 扫描dao或mapper接口
public class DataSourceTestConfig {


    @Bean(name = "dataSourceTest")
    public DataSource dataSourceTest(DataSourceTestProperties dataSourceTestProperties){
        DruidXADataSource dataSource = new DruidXADataSource();
        BeanUtils.copyProperties(dataSourceTestProperties,dataSource);
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(dataSource);
        xaDataSource.setUniqueResourceName("dataSourceTest");
        return xaDataSource;
    }

    @Bean(name = "sqlSessionFactoryTest")
    public SqlSessionFactory sqlSessionFactoryTest(@Qualifier("dataSourceTest") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage("com.lzhsite.spring.web.entity.test");
        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/test/*Mapper.xml"));
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplateTest")
    public SqlSessionTemplate sqlSessionTemplateTest(
            @Qualifier("sqlSessionFactoryTest") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
