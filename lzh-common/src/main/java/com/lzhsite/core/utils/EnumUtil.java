package com.lzhsite.core.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lzhcode on 2018/4/3.
 */
public class EnumUtil {

    /**
     * indexOf,传入的参数ordinal指的是需要的枚举值在设定的枚举类中的顺序，以0开始
     * T
     * @param clazz
     * @param ordinal
     * @return
     * @author   xiehao
     */
    public static <T extends Enum<T>> T indexOf(Class<T> clazz, int ordinal){
        return (T) clazz.getEnumConstants()[ordinal];
    }
    /**
     * nameOf,传入的参数name指的是枚举值的名称，一般是大写加下划线的
     * T
     * @param clazz
     * @param name
     * @return Enum T
     * @author   xiehao
     */
    public static <T extends Enum<T>> T nameOf(Class<T> clazz, String name){

        return (T) Enum.valueOf(clazz, name);
    }
    /**
     * 使用枚举类型对应的code获取枚举类型
     * T
     * @param clazz    枚举类的class
     * @param getcodeMethodName  传入的code的get方法
     * @param code  传入的code值，这个方法为String类型
     * @return
     * @author   xiehao
     */
    public static <T extends Enum<T>> T getByStringcode(Class<T> clazz,String getcodeMethodName, String code){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getcodeMethodName);
            String codeVal = null;
            for(T entity:arr){
                codeVal = targetMethod.invoke(entity).toString();
                if(codeVal.contentEquals(code)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用枚举类型对应的code获取枚举类型
     * T
     * @param clazz    枚举类的class
     * @param getcodeMethodName  传入的code的get方法
     * @param code  传入的code值，这个方法为Integer类型
     * @return
     * @author   xiehao
     */
    public static <T extends Enum<T>> T getByIntegercode(Class<T> clazz,String getcodeMethodName, Integer code){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getcodeMethodName);
            Integer codeVal = null;
            for(T entity:arr){
                codeVal = Integer.valueOf(targetMethod.invoke(entity).toString());
                if(codeVal.equals(code)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 使用枚举类型对应的typeName获取枚举类型
     * T
     * @param clazz   枚举类的class
     * @param getTypeNameMethodName  传入的typeName的get方法
     * @param typeName 传入的typeName值，这个方法为String类型
     * @return
     * @author   xiehao
     */
    public static <T extends Enum<T>> T getByStringTypeName(Class<T> clazz,String getTypeNameMethodName, String typeName){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getTypeNameMethodName);
            String typeNameVal = null;
            for(T entity:arr){
                typeNameVal = targetMethod.invoke(entity).toString();
                if(typeNameVal.contentEquals(typeName)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
}
