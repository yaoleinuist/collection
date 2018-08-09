package com.lzhsite.zookeeper.rmi;

import java.io.Serializable;
import java.rmi.Remote;

public interface Service extends Remote,Serializable{

    public void service(String name);
    
}
