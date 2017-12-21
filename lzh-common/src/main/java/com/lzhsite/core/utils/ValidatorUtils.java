package com.lzhsite.core.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * Created by Liling on 2016/4/1.
 */
public class ValidatorUtils {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 验证参数对象内的属性
     * @param param 参数对象
     * @param methodAnnotation 分组校验注解类
     * @return NULL:验证成功；非NULL值：错误编码
     */
    public static String validateParamsProperty(Object param, Class methodAnnotation){
        Set<ConstraintViolation<Object>> violations = validator.validate(param, Default.class, methodAnnotation);
        if (CollectionUtils.isNotEmpty(violations)) {
            return violations.iterator().next().getMessage();
        }
        return null;
    }


    /**
     * 验证参数对象内的属性
     * @param param 参数对象
     * @return NULL:验证成功；非NULL值：错误编码
     */
    public static String validateParamsProperty(Object param){
        Set<ConstraintViolation<Object>> violations = validator.validate(param, Default.class);
        if (CollectionUtils.isNotEmpty(violations)) {
            return violations.iterator().next().getMessage();
        }
        return null;
    }
}
