package com.lzhsite.core.datasource;

import javax.sql.DataSource;


/**
 * 设置主从
 * 
 */
public class DataSourceHolder {

    private static final String MASTER = "master";

    private final static String SLAVE = "slave";

    /**
     * dataSource master or slave
     */
    private final static ThreadLocal<String> dataSources = new ThreadLocal<String>();

    /**
     * master local
     */
    private final static ThreadLocal<DataSource> masterLocal = new ThreadLocal<DataSource>();

    private final static ThreadLocal<DataSource> slaveLocal = new ThreadLocal<DataSource>();

    /**
     * 设置数据源
     *
     * @param dataSourceKey
     */
    private static void setDataSource(String dataSourceKey) {
        dataSources.set(dataSourceKey);
    }

    private static String getDataSource() {
        return dataSources.get();
    }

    public static void setMaster() {
        dataSources.set(MASTER);
    }

    public static void setSlave() {
        dataSources.set(SLAVE);
    }

    /**
     *
     * 将master放入threadlocal
     *
     * @param master
     */
    public static void setMaster(DataSource master) {
        masterLocal.set(master);
    }

    /**
     * 将slave放入threadlocal
     *
     * @param slave
     */
    public static void setSlave(DataSource slave) {
        slaveLocal.set(slave);
    }

    public static boolean isMaster() {
        return MASTER.equals(dataSources.get());
    }

    public static boolean isSlave() {
        return SLAVE.equals(dataSources.get());
    }

    
    public static String  getDatasourcesType() {
		return dataSources.get();
	}

	/**
     * 清除thread local中的数据源
     */
    public static void clearDataSource() {
        dataSources.remove();
        masterLocal.remove();
        slaveLocal.remove();
    }
}
