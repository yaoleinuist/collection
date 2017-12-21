package com.lzhsite.ensure.extensions;

import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.ensure.EnsureParam;

/**
 * Created by Jintao on 2015/6/9.
 */
public class EnsureParamBooleanExtension extends EnsureParam<Boolean> {
    private Boolean condition;

    public EnsureParamBooleanExtension(Boolean condition) {
        super(condition);
        this.condition = condition;
    }

    public EnsureParamBooleanExtension isFalse(String errorCode){
        if(condition){
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }

    public EnsureParamBooleanExtension isTrue(String errorCode){
        if(!condition){
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }

    public EnsureParamBooleanExtension isNotNull(String errorCode) {
        if (condition == null) {
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }

}
