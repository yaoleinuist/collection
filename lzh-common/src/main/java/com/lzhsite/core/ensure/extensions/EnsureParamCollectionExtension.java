package com.lzhsite.core.ensure.extensions;

import java.util.Collection;

import com.lzhsite.core.exception.XExceptionFactory;

/**
 * Created by Jintao on 2015/6/8.
 */
public class EnsureParamCollectionExtension extends EnsureParamObjectExtension {

    private Collection collection;
    public EnsureParamCollectionExtension(Collection collection) {
        super(collection);
        this.collection =collection;
    }

    public EnsureParamCollectionExtension isNotEmpty(String errorCode){
        if(collection != null && collection.size() > 0){
            return this;
        } else {
            throw XExceptionFactory.create(errorCode);
        }
    }

    public EnsureParamCollectionExtension isNotNull(String errorCode) {
        if(collection == null){
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }

    public EnsureParamCollectionExtension isEqualTo(Collection obj, String errorCode) {
        if (!(collection == null ? obj == null : collection.equals(obj))) {
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }

    public EnsureParamCollectionExtension isNotEqualTo(Collection obj, String errorCode) {
        if (collection == null ? obj == null : collection.equals(obj)) {
            throw XExceptionFactory.create(errorCode);
        }
        return this;
    }
}
