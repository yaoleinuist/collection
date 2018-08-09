package com.lzhsite.zookeeper.rmi;

import java.rmi.Remote;

public class RMIHelper {
    
    private static final RMIServer SERVER = new RMIServer();
    
    private static final RMIClient CLIENT = new RMIClient();
    
    public static synchronized void publish(Remote remote,String key){
        SERVER.publish(remote, key);
    }
    
    public static synchronized <T extends Remote> T lookup(String key){
        return CLIENT.lookup(key);
    }
    
    public static void main(String[] args) throws Exception {
        while(true){
            Service service = RMIHelper.lookup("Test");
            service.service("test12");
            Service service1 = RMIHelper.lookup("Test1");
            service1.service("test12");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
}