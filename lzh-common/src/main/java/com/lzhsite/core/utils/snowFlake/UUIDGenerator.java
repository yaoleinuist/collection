package com.lzhsite.core.utils.snowFlake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;


/**
 * 分库分表之后分布式下如何保证ID全局唯一性
 */
public class UUIDGenerator {
    private final static Logger LOGGER = LoggerFactory.getLogger(UUIDGenerator.class);
    private final static Long datacenterId = 1L;//数据中心
    private static Long machineId = null;//机器号
    private static SnowFlake snowFlake;

    public static Long getNextId() throws Exception{
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
     * @throws Exception
     */
    public static synchronized SnowFlake getInstance() throws Exception {
        if (snowFlake == null) {
            if(null == machineId){
                long machinePiece;
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> e = null;
                try {
                    e = NetworkInterface.getNetworkInterfaces();
                } catch (SocketException e1) {
                    e1.printStackTrace();
                }
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    sb.append(ni.toString());
                }
                machinePiece = sb.toString().hashCode();
                long  machineMask= -1L ^ (-1L << SnowFlake.getMachineBit());
                //控制机器号的位数
                machineId = machinePiece & machineMask;
            }
            snowFlake = new SnowFlake(datacenterId, machineId);
        }
        return snowFlake;
    }


    public static void main(String[] args) throws Exception {


        for (int i = 0; i < (1 << 20); i++) {
            System.out.println(UUIDGenerator.getNextId());
        }

    }
}
