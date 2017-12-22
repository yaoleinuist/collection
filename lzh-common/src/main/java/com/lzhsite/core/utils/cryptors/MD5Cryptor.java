package com.lzhsite.core.utils.cryptors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Cryptor {
    private MD5Cryptor() {
    }

    private static MD5Cryptor instance = new MD5Cryptor();

    public String md5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5实例初始化失败", e);
        }
        byte[] byteArray = messageDigest.digest();

        return bufferToHex(byteArray);
    }

    /**
     * 计算文件的MD5
     *
     * @param fileName 文件的绝对路径
     * @return
     */
    public String getFileMD5String(String fileName) {
        File f = new File(fileName);
        return getFileMD5String(f);
    }

    /**
     * 计算文件的MD5，重载方法
     *
     * @param file 文件对象
     * @return
     */
    public String getFileMD5String(File file) {
        MessageDigest messageDigest = null;
        String hex = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream stream = new FileInputStream(file);
            FileChannel ch = stream.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messageDigest.update(byteBuffer);
            hex = bufferToHex(messageDigest.digest());
            stream.close();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5实例初始化失败", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件找不到", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return hex;
    }

    public static MD5Cryptor getInstance() {
        return instance;
    }

    @Deprecated
    public String getMD5String(byte[] bytes) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5实例初始化失败", e);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        messageDigest.update(byteBuffer);
        byte[] bs = messageDigest.digest();
        return bufferToHex(bs);
    }

    private String bufferToHex(byte byteArray[]) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                builder.append('0').append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                builder.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return builder.toString();
    }

}