package com.lzhsite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.log4j.Logger;
import com.lzhsite.core.annotations.RateLimit;


@RestController
public class ShopOrderController {
	private static final Logger logger = Logger.getLogger(ShopOrderController.class);
 

    @RequestMapping("/seckill")
    @RateLimit
    public String index(Long stockId) {
     
        return "";
    }
}

