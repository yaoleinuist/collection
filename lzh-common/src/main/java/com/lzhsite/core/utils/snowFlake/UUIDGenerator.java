package com.lzhsite.core.utils.snowFlake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.exception.XBusinessException;
import com.lzhsite.core.exception.XExceptionFactory;

/**
 * 分库分表之后分布式下如何保证ID全局唯一性
 */
public class UUIDGenerator {
    private final static Logger LOGGER = LoggerFactory.getLogger(UUIDGenerator.class);
    private final static Long datacenterId = 1l;//数据中心
    private static Long machineId = null;//机器号
    private static SnowFlake snowFlake;

    public static Long getNextId() throws XBusinessException{
        SnowFlake snowFlake = getInstance();
        return snowFlake.nextId();
    }

    /**
     * 设置机器号
     * @param machineId
     */
    public static synchronized void setMachineId(Long machineId){
        UUIDGenerator.machineId = machineId;
    }

    public static Long getMachineId() {
        return machineId;
    }

    /**
     * 单例SnowFlake
     * @return
     * @throws XBusinessException 
     */
    public static synchronized SnowFlake getInstance() throws XBusinessException {
        if (snowFlake == null) {
            if(null == machineId){
                throw XExceptionFactory.create("WEMAILL_SERIAL_1001");
            }
            snowFlake = new SnowFlake(datacenterId, machineId);
        }
        return snowFlake;
    }


    public static void main(String[] args) throws XBusinessException{
        UUIDGenerator.setMachineId(1l);
        for (int i = 0; i < (1 << 20); i++) {
            System.out.println(UUIDGenerator.getNextId());
        }

    }
}
