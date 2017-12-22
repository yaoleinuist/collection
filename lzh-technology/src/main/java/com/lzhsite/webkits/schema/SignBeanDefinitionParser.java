package com.lzhsite.webkits.schema;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lzhsite.core.utils.ReflectionUtils;
import com.lzhsite.webkits.security.signature.SignatureConfig;



public class SignBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String ELEMENT_IGNORE = "ignore";

    protected Class getBeanClass(Element element) {
        return SignatureConfig.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        if(element.hasAttribute("type")){
            bean.addPropertyValue("type", element.getAttribute("type"));
        }
        if(element.hasAttribute("skipCheck")){
            bean.addPropertyValue("skipCheckSignature", element.getAttribute("skipCheck"));
        }
        if(element.hasAttribute("skipWrite")){
            bean.addPropertyValue("skipWriteSignature", element.getAttribute("skipWrite"));
        }
        if(element.hasAttribute("signatureService-ref")){
            bean.addPropertyReference("signatureService", element.getAttribute("signatureService-ref"));
        }
        if(element.hasAttribute("signatureService-type")){
            Object  o = ReflectionUtils.instance(element.getAttribute("signatureService-type"));
            bean.addPropertyValue("signatureService", o);
        }
        NodeList nodes = element.getChildNodes();
        List<String> ignoreList = new ArrayList<>();
        for(int i = 0; i < nodes.getLength(); i++){
            Node node = nodes.item(i);
            if(!isIgnoreElement(node)){
                continue;
            }
            String ignoreUrl = node.getNodeValue();
            ignoreList.add(ignoreUrl);
        }
        bean.addPropertyValue("ignoreList", ignoreList);
    }

    private boolean isIgnoreElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE && ELEMENT_IGNORE.equals(node.getLocalName());
    }

}
