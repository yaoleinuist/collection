package com.lzhsite.webkits.interceptors.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交注解，用于方法上
 * 步骤一:  在新建页面方法上，设置needSaveToken()为true，此时拦截器会在Session中保存一个token，
 * 步骤二:  在新建的页面中添加 <input type="hidden" name="token" value="${token}"><input type="hidden" name="tokenKey" value="${tokenKey}">
 * 步骤三:  保存方法需要验证重复提交的，设置needRemoveToken为true,此时会在拦截器中验证是否重复提交
 * Created by ylc on 2015/7/8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidDuplicateSubmission {

    /**
     * 注解，添加token
     */
    boolean needSaveToken() default false;

    /**
     * 注解，验证token，后移除token
     */
    boolean needRemoveToken() default false;

    /**
     * 验证失败跳转页
     */
    String  pageUrl() default "";

}
