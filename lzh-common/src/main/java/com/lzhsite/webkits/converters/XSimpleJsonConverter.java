package com.lzhsite.webkits.converters;


/**
 * Created by nt on 2017-06-19.
 */
public class XSimpleJsonConverter extends BaseHttpMessageConverter {

    @Override
    protected Object doWrite(Object obj) {
        return obj;
    }

}
