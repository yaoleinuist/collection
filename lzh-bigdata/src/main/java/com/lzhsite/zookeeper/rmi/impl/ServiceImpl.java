package com.lzhsite.zookeeper.rmi.impl;

import com.lzhsite.zookeeper.rmi.Service;

public class ServiceImpl implements Service{

    @Override
    public void service(String name) {
        System.out.println(name);
    }

}