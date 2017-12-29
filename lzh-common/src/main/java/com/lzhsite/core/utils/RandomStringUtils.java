package com.lzhsite.core.utils;
 

import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.util.*;

public class RandomStringUtils extends org.apache.commons.lang3.RandomStringUtils{

    //默认随机生成的uuuid长度
    public static final int DEFAULT_UUUID_LENGTH = 32;

    public static String generateSalt() {
        return new SecureRandomNumberGenerator().nextBytes().toBase64();

    }

    public static String generateRandomCharAndNumber(int count) {
        if (count < 2) {
            throw new IllegalArgumentException(
                    "Requested random string length " + count
                            + " is less than 1.");
        }
        int numberCount = RandomUtils.nextInt(count - 1);
        numberCount = numberCount == 0 ? 1 : numberCount;
        StringBuilder stringBuilder = new StringBuilder();
        // 生成随机数字
        stringBuilder.append(randomNumeric(numberCount));
        // 生成随机字符(剔除'l','o')
        stringBuilder.append(random((count - numberCount),
                "abcdefghijkmnpqrstuvwxyz"));
        // 打乱
        char[] c = stringBuilder.toString().toCharArray();
        List<Character> lst = new ArrayList<Character>();
        for (int i = 0; i < c.length; i++)
            lst.add(c[i]);
        Collections.shuffle(lst);
        stringBuilder = new StringBuilder();
        for (Character character : lst)
            stringBuilder.append(character);
        return stringBuilder.toString();
    }

    public static String getRandomString() {
        return getRandomString(DEFAULT_UUUID_LENGTH);
    }

    public static String getRandomString(int length){
        return getRandomString(length, Boolean.FALSE);
    }
    
    public static String getRandomString(int length, Boolean upper){
        StringBuilder builder = new StringBuilder();
        String uuid = getUUID();
        if(uuid.length() >= length){
            builder.append(uuid.substring(0, length));
        }else{
            builder.append(uuid);
            Random random = new Random();
            while(builder.length() < length){
                int i = random.nextInt(36);
                builder.append(i>25?(char)('0'+(i-26)):(char)('a'+i));
            }
        }
        if(upper) {
            return builder.toString().toUpperCase();
        }
        return builder.toString();
    }
    
    public static String getUUID(){
        UUID uuid  =  java.util.UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
