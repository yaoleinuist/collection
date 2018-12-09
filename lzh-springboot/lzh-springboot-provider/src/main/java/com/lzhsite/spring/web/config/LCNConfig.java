package com.lzhsite.spring.web.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.codingapi.tx.config.service.TxManagerTxUrlService;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@EnableTransactionManagement(proxyTargetClass=true)
@ComponentScan(basePackages={"com.codingapi.tx.*","com.lzhsite.spring.*"})
public class LCNConfig implements TxManagerTxUrlService{


    @Value("${tm.manager.url}")
    private String url;

    @Override
    public String getTxUrl() {
        System.out.println("load tm.manager.url ");
        return url;
    }
    
    

}
