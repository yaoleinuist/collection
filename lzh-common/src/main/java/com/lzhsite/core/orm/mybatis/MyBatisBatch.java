package com.lzhsite.core.orm.mybatis;

import java.util.Objects;
import java.util.function.Consumer;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.context.ApplicationContextHelper;
import com.lzhsite.core.exception.XBusinessException;
import com.lzhsite.core.exception.XExceptionFactory;

/**
 * MyBatis 批量插入
 * Created by Liling on 2016/5/31.
 */
public class MyBatisBatch {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisBatch.class);

    private static SqlSessionFactory sqlSessionFactory = ApplicationContextHelper.getContext().getBean("sqlSessionFactory", SqlSessionFactory.class);

    public static <T> void doBatch(Class<T> daoClass, Consumer<T> consumer) throws XBusinessException{
        Objects.requireNonNull(consumer);
        if (sqlSessionFactory == null) {
            logger.error("无法获取mybatis sqlSessionFactory,请检查mybatis配置");
            throw XExceptionFactory.create("F_CORE_DS_1004");
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            T mapper = sqlSession.getMapper(daoClass);
            consumer.accept(mapper);
            sqlSession.commit();
            sqlSession.clearCache();
        } catch (Exception e) {
            logger.error("批量更新失败", e);
            throw XExceptionFactory.create("F_CORE_DS_1004");
        }
    }

}
