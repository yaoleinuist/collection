package com.lzhsite.util.cryptors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import com.lzhsite.core.utils.EnCryptors;

import sun.misc.BASE64Decoder;

/**
 * Created by ylc on 2015/11/30.
 */
public class RSACryptor {

    private static final String algorithm = "RSA";
    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private RSACryptor() {
    }

    private static RSACryptor instance = new RSACryptor();

    public static RSACryptor getInstance() {
        return instance;
    }

    /**
     * 分段加密
     *
     * @param privateKeyStr 加密的私钥
     * @param data          待加密的明文数据
     * @return 加密后的数据
     */
    public String encrypt(String privateKeyStr, byte[] data) {
        try {
            /** 生成私钥 */
            RSAPrivateKey privateKey = getRSAPrivateKey(privateKeyStr);

            Cipher cipher = Cipher.getInstance("RSA",
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
            // 加密块大小为127
            // byte,加密后为128个byte;因此共有2个加密块，第一个127
            // byte第二个为1个byte
            int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize)
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i
                            * outputSize);
                else
                    cipher.doFinal(data, i * blockSize, data.length - i
                            * blockSize, raw, i * outputSize);
                i++;
            }
            //return raw;
            return new String(EnCryptors.BASE64.encode(raw, false), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("数据内容填充错误", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("私钥非法", e);
        } catch (ShortBufferException e) {
            throw new RuntimeException("数据缓存区异常", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("数据块大小异常", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("数据内容填充错误", e);
        }
    }

    /**
     * 分段解压
     *
     * @param publicKeyStr 解压的公钥
     * @param raw          已经加密的数据
     * @return 解压后的明文
     */
    public byte[] decrypt(String publicKeyStr, byte[] raw) {
        try {
            /** 生成公钥 */
            RSAPublicKey publicKey = getRSAPublicKey(publicKeyStr);

            Cipher cipher = Cipher.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(cipher.DECRYPT_MODE, publicKey);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;

            raw = EnCryptors.BASE64.decode(raw);

            while (raw.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (IOException e) {
            throw new RuntimeException("数据内容读取错误", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("公钥非法", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("数据内容填充错误", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("数据内容填充错误", e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("数据块大小异常", e);
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return 公钥
     */
    public RSAPublicKey getRSAPublicKey(String publicKeyStr) {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("公钥非法", e);
        } catch (IOException e) {
            throw new RuntimeException("公钥数据内容读取错误", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("公钥数据为空", e);
        }
    }

    /**
     * 从字符串加载私钥
     *
     * @param privateKeyStr 私钥数据字符串
     * @return 私钥
     */
    public RSAPrivateKey getRSAPrivateKey(String privateKeyStr) {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("私钥非法", e);
        } catch (IOException e) {
            throw new RuntimeException("私钥数据内容读取错误", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("私钥数据为空", e);
        }
    }

    public String sign(String content, String privateKeyStr) {
        try {
            /** 生成私钥 */
            RSAPrivateKey privateKey = getRSAPrivateKey(privateKeyStr);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();
            return new String(EnCryptors.BASE64.encode(signed, false), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("私钥非法", e);
        }


    }

    /**
     * @param data         数据
     * @param sign         签名
     * @param publicKeyStr 公钥
     * @return
     */
    public boolean verify(String data, String sign, String publicKeyStr) {
        return verify(data.getBytes(StandardCharsets.UTF_8), sign, publicKeyStr);
    }

    /**
     * @param data         数据
     * @param sign         签名
     * @param publicKeyStr 公钥
     * @return
     */
    public boolean verify(byte[] data, String sign, String publicKeyStr) {
        try {
            /** 生成公钥 */
            RSAPublicKey publicKey = getRSAPublicKey(publicKeyStr);
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(data);

            return signature.verify(EnCryptors.BASE64.decode(sign));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("公钥非法", e);
        }

    }
}
