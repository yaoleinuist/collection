//package com.lzhsite.spring.web.config;
//
//import javax.sql.DataSource;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.context.annotation.Primary;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import com.alibaba.druid.pool.xa.DruidXADataSource;
//
///**
// * Created by Administrator on 2017/3/26/026.
// * 数据源配置
// */
//@Configuration
//@EnableTransactionManagement
//@EnableAspectJAutoProxy(exposeProxy=true,proxyTargetClass=true)
//@MapperScan(basePackages = {"com.lzhsite.spring.web.mapper.db0*"}, sqlSessionTemplateRef = "sqlSessionTemplateDB0") // 扫描dao或mapper接口
//public class DataSourceDB0Config {
//
//    @Primary
//    @Bean(name = "dataSourceDB0")
//    public DataSource dataSourceDB0(DataSourceDB0Properties dataSourceDB1Properties){
//        DruidXADataSource dataSource = new DruidXADataSource();
//        BeanUtils.copyProperties(dataSourceDB1Properties,dataSource);
////        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
////        xaDataSource.setXaDataSource(dataSource);
////        xaDataSource.setUniqueResourceName("dataSourceDB0");
//        return dataSource;
//    }
//
//
//    @Bean(name = "sqlSessionFactoryDB0")
//    public SqlSessionFactory sqlSessionFactoryDB0(@Qualifier("dataSourceDB0") DataSource dataSource)
//            throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        bean.setTypeAliasesPackage("com.lzhsite.spring.web.entity.db0");
//        //bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/DB0/*Mapper.xml"));
//        return bean.getObject();
//    }
//
//    @Bean(name = "sqlSessionTemplateDB0")
//    public SqlSessionTemplate sqlSessionTemplateDB0(
//            @Qualifier("sqlSessionFactoryDB0") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
