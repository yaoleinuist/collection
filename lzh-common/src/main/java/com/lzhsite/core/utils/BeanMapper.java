package com.lzhsite.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.dozer.DozerBeanMapper;

import com.google.common.collect.Lists;

/**
 * Created by lyl on 2016/10/10.
 */
public class BeanMapper {

    /**
     * dozer单例
     */
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    /**
     * 对象间的值拷贝
     * @param source
     * @param destination
     * @param <T>
     * @return
     */
    public static <T> T map(Object source, Class<T> destination){
        return dozer.map(source, destination);
    }

    /**
     * list之间的值拷贝
     * @param sourceList
     * @param destinationClass
     * @param <T>
     * @return
     */
    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 基本类型之间的赋值
     * @param source
     * @param destinationObject
     */
    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }

    /**
     * 内部类之间的拷贝
     * @param source
     * @param <T>
     * @return
     */
  public static <T, N> T mapInnerObject(Object source, Class<N> outerClass, Class<T> innerClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class clazz = MineHomeResponse.class;
        N response = (N) outerClass.newInstance();
        Class innerClazz[] = outerClass.getDeclaredClasses();
        T innerInstance = null;
        for (Class cls : innerClazz) {
            if (cls.equals(innerClass)){
                // 构造成员内部类实例
                Constructor constructor = cls.getDeclaredConstructor(innerClass);
                constructor.setAccessible(true);
                innerInstance = (T) constructor.newInstance(response);
            }
        }
        BeanUtils.copyProperties(source, innerInstance);
        return innerInstance;
    } 

}
