package com.lzhsite.core.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * https://www.cnblogs.com/surge/p/3582248.html
 * 
 * Created by pangpeijie on 16/11/11.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicInteger counter = new AtomicInteger();

    /**
     * master库 dataSource
     */
    private DataSource master;

    /**
     * 多个slave
     */
    private List<DataSource> slaves;


    @Override
    protected Object determineCurrentLookupKey() {
        //do nothing
        return null;
    }

    @Override
    public void afterPropertiesSet(){
        //do nothing
    }

    /**
     * 切换数据源
     */
    @Override
    protected DataSource determineTargetDataSource() {
        DataSource returnDataSource = null;
        if (DataSourceHolder.isMaster()) {
            returnDataSource = master;
        } else if (DataSourceHolder.isSlave()) {
            //如果从库有多个,则随机分配
            int count = counter.incrementAndGet();
            if (count > 1000000) {
                counter.set(0);
            }
            int n = slaves.size();
            int index = count % n;
            returnDataSource = slaves.get(index);
        } else {
            returnDataSource = master;
        }

        if (returnDataSource instanceof com.alibaba.druid.pool.DruidDataSource) {
            DruidDataSource source = (DruidDataSource) returnDataSource;
            String jdbcUrl = source.getUrl();
            logger.info("切换datasourse为{},jdbcUrl:{}",DataSourceHolder.isMaster()==true?"Master":"Slave",jdbcUrl);
        }
        return returnDataSource;
    }

    public DataSource getMaster() {
        return master;
    }

    public void setMaster(DataSource master) {
        this.master = master;
    }

    public List<DataSource> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<DataSource> slaves) {
        this.slaves = slaves;
    }
}
