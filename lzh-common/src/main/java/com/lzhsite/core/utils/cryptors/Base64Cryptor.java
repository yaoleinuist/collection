package com.lzhsite.core.utils.cryptors;

import java.nio.charset.StandardCharsets;

import com.lzhsite.core.utils.cryptors.codec.Base64;

public class Base64Cryptor {
    private static Base64Cryptor instance = new Base64Cryptor();

    /**
     * Base64编码
     *
     * @param data byte
     * @return
     */
    public byte[] encode(byte[] data) {
        return encode(data, true);
    }

    /**
     * Base64编码
     *
     * @param data 字符串
     * @return
     */
    public byte[] encode(String data) {
        return encode(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64编码
     *
     * @param data      字符串
     * @param isChunked 如果为true则76个字符换行
     * @return
     */
    public byte[] encode(String data, boolean isChunked) {
        return encode(data.getBytes(StandardCharsets.UTF_8), isChunked);
    }

    /**
     * Base64编码
     *
     * @param data      byte
     * @param isChunked 如果为true则76个字符换行
     * @return
     */
    public byte[] encode(byte[] data, boolean isChunked) {
        return Base64.encodeBase64(data, isChunked);
    }

    /**
     * Base64解码
     *
     * @param data byte
     * @return
     */
    public byte[] decode(byte[] data) {
        return Base64.decodeBase64(data);
    }


    /**
     * Base64解码
     *
     * @param data 字符串
     * @return
     */
    public byte[] decode(String data) {
        return decode(data.getBytes(StandardCharsets.UTF_8));
    }

    private Base64Cryptor() {
    }

    public static Base64Cryptor getInstance() {

        return instance;

    }

}