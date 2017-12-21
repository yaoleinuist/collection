package com.lzhsite.core.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 唯一id生成器
 * Created by lll on 16/11/21.
 */
public final class IdGenerator {

    private static final String date_format = "yyMMddHHmmss";
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();

    private static IdWorker idWorker = new IdWorker();
    private static ObjectId objectId = new ObjectId();


    /**
     * 获取纯数字唯一ID
     */
    public static Long getUniqueCode() {
        return idWorker.nextId();
    }

    /**
     * 获取字符型唯一ID
     * 返回28位长字符串，字符串组成：[12位时间yyMMddHHmmss][16位机器标识+进程id标识+自增id]
     */
    public static String getUniqueCodeStr(){
        StringBuilder buf = new StringBuilder(28);
        buf.append(formatDate(new Date()));
        buf.append(objectId.nextId());
        return buf.toString();
    }

    private static DateFormat getDateFormat() {
        DateFormat df = threadLocal.get();
        if(df == null){
            df = new SimpleDateFormat(date_format);
            threadLocal.set(df);
        }
        return df;
    }

    private static String formatDate(Date date){
        return getDateFormat().format(date);
    }

    /**
     * 借鉴twitter的Snowflake算法（生成纯数字ID）
     * 保持时间上有序,且基本能单调递增,不重复
     * Created by Guoqw
     * Updated by lll on 16/11/22.
     */
    private static class IdWorker {
        private static final long twepoch = 1479785410983L;
        private static final long ipBits = 8L;
        private static final long sequenceBits = 12L;
        private static final long ipShift = sequenceBits;
        private static final long timestampLeftShift = sequenceBits + ipBits;
        private static final long sequenceMask = ~(-1L << sequenceBits);
        private static final long ip;

        private long sequence = 0L;
        private long lastTimestamp = -1L;

        synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - twepoch) << timestampLeftShift) | (ip << ipShift) | sequence;
        }

        static {
            try{
                String[] arr = getIp().split("\\.");
                String str = String.format("%03d", Integer.valueOf(arr[3]));
                ip = Long.parseLong(str);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 获取本地ip地址
         */
        static String getIp() {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        long timeGen() {
            return System.currentTimeMillis();
        }

    }

    /**
     * 借鉴MongoDB的ObjectId生成唯一ID（字符型）
     * 返回16位字符串，由机器标识+进程id标识+自增id组成
     */
    private static class ObjectId {

        private static final int machine;
        private static final AtomicInteger nextInc = new AtomicInteger((new Random()).nextInt());

        private int sequence = 0;

        /**
         * Create a new object id.
         */
        synchronized String nextId() {
            sequence = nextInc.getAndIncrement();
            if(sequence < 0){
                nextInc.set(0);
                sequence = nextInc.getAndIncrement();
            }
            return toHexString();
        }

        /**
         * Converts this instance into a 24-byte hexadecimal string representation.
         *
         * @return a string representation of the ObjectId in hexadecimal format
         */
        String toHexString() {
            final StringBuilder buf = new StringBuilder(16);
            for (final byte b : toByteArray()) {
                buf.append(String.format("%02x", b & 0xff));
            }
            return buf.toString();
        }

        /**
         * Convert to a byte array.  Note that the numbers are stored in big-endian order.
         *
         * @return the byte array
         */
        byte[] toByteArray() {
            byte b[] = new byte[8];
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.putInt(machine);
            bb.putInt(sequence);
            return b;
        }

        static {
            try {
                // build a 2-byte machine piece based on NICs info
                int machinePiece;
                try {
                    StringBuffer sb = new StringBuffer();
                    Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                    while (e.hasMoreElements()) {
                        sb.append(e.nextElement());
                    }
                    machinePiece = sb.toString().hashCode() << 16;
                } catch (Throwable e) {
                    // exception sometimes happens with IBM JVM, use random
                    machinePiece = (new Random().nextInt()) << 16;
                }

                // add a 2 byte process piece. It must represent not only the JVM but the class loader.
                // Since static var belong to class loader there could be collisions otherwise
                final int processPiece;
                int processId;
                try {
                    processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
                } catch (Throwable t) {
                    processId = new Random().nextInt();
                }

                ClassLoader loader = IdGenerator.class.getClassLoader();
                int loaderId = loader != null ? System.identityHashCode(loader) : 0;

                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toHexString(processId));
                sb.append(Integer.toHexString(loaderId));
                processPiece = sb.toString().hashCode() & 0xFFFF;

                machine = machinePiece | processPiece;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            long code = IdGenerator.getUniqueCode();
            System.out.println("code:" + code + ", " + Long.toBinaryString(code));
        }
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j = 0; j < 100; j++) {
                        String id = IdGenerator.getUniqueCodeStr();
                        System.out.println(Thread.currentThread().getName() + ",j:" + j + ":" + id);
                    }
                }
            }).start();
        }
    }

}
