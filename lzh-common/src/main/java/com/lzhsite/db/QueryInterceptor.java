package com.lzhsite.db;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.lzhsite.core.utils.ReflectionUtils;
 

/**
 * <p>
 * 数据库分页和排序插件，只拦截查询语句.
 * </p>
 *
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class QueryInterceptor implements Interceptor, Serializable {

    private static final long serialVersionUID = 4235855564540389507L;

    protected Dialect DIALECT = new MySQLDialect();

    /**
     * 拦截的ID，在mapper中的id，可以匹配正则
     */
    protected String _SQL_PATTERN = ".*query.*";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectionUtils.getFieldValue(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
            if (mappedStatement.getId().matches(_SQL_PATTERN)) { //拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                String originalSql = boundSql.getSql();
                if(StringUtils.isBlank(originalSql)){
                    return invocation.proceed();
                }

                Map parameterObject = (Map) boundSql.getParameterObject();
                //查询参数--上下文传参
                Pager pager = SQLHelp.getPager(parameterObject);
                if (pager != null) {
                    //处理排序
                    SQLHelp.initPagination(pager);
                    originalSql = SQLHelp.generateOrderSql(originalSql, pager, DIALECT);
                    String pageSql = SQLHelp.generatePageSql(originalSql, pager, DIALECT);
                    ReflectionUtils.setFieldValue(boundSql,"sql",pageSql);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
