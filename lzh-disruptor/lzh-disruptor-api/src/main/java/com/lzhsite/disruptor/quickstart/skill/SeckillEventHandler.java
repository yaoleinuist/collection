package com.lzhsite.disruptor.quickstart.skill;

import com.lmax.disruptor.EventHandler;

public class SeckillEventHandler implements EventHandler<SeckillEvent>{
	   //业务处理、这里是无法注入的，需要手动获取，见源码
//    private ISeckillService seckillService = (ISeckillService) SpringUtil.getBean("seckillService");
//    
    public void onEvent(SeckillEvent seckillEvent, long seq, boolean bool) throws Exception {
        //seckillService.startSeckil(seckillEvent.getSeckillId(), seckillEvent.getUserId());
    }
}
