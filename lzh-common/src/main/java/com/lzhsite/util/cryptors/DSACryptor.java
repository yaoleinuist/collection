package com.lzhsite.util.cryptors;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.lzhsite.core.utils.EnCryptors;

/**
 * Created by ylc on 2015/11/30.
 */
public class DSACryptor {

    private static final String algorithm = "DSA";

    private DSACryptor() {
    }

    private static DSACryptor instance = new DSACryptor();

    public static DSACryptor getInstance() {
        return instance;
    }

    public String sign(String signingStr, String privateKey) {
        return sign(signingStr.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    /**
     * DSA签名
     *
     * @param data       要签名的数据
     * @param privateKey 私钥
     * @return Base64加密后的字符串
     */
    public String sign(byte[] data, String privateKey) {
        try {
            byte[] keyBytes = EnCryptors.BASE64.decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(algorithm);

            /** 生成私钥 */
            PrivateKey priKey = factory.generatePrivate(keySpec);

            /** 用私钥对信息进行数字签名 */
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(data);

            return new String(EnCryptors.BASE64.encode(signature.sign(), false), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("公钥非法", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("公钥非法", e);
        }

    }

    public boolean verify(String data, String sign, String publicKey) {
        return verify(data.getBytes(StandardCharsets.UTF_8), sign, publicKey);
    }

    /**
     * @param data
     * @param publicKey
     * @param sign
     * @return
     */
    public boolean verify(byte[] data, String sign, String publicKey) {
        try {
            byte[] keyBytes = EnCryptors.BASE64.decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            /** 生成公钥 */
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(data);

            /** 验证签名 */
            return signature.verify(EnCryptors.BASE64.decode(sign));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无此算法", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名异常", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("公钥非法", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("公钥非法", e);
        }


    }
}
