package com.lzhsite.webkits.annotations;

import java.lang.annotation.*;

/**
 * Created by Jintao on 2016/7/16.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWithoutResult {
    // 这个annotation 必须申明在@ResponseBody之前
}
