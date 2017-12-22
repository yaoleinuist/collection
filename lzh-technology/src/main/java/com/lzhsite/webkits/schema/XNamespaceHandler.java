package com.lzhsite.webkits.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * http://blog.csdn.net/wujiang88/article/details/51992836
 * http://blog.csdn.net/54powerman/article/details/60956826
 * http://sammor.iteye.com/blog/1106254
 * 
 * Created by liuliling on 17/6/20.
 */
public class XNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("sign", new SignBeanDefinitionParser());
    }

}
