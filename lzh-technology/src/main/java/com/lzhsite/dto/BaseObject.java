package com.lzhsite.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BaseObject implements Serializable {
    public BaseObject() {
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
