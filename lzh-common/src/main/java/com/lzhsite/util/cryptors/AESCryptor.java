package com.lzhsite.util.cryptors;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.lzhsite.core.utils.EnCryptors;

public class AESCryptor {
	private AESCryptor(){}
	private static AESCryptor instance=new AESCryptor();
    
    /**
     * The salt must be 128 bit, saying 16 characters
     */
    private  String DEFAULT_SALT = "xpos1qazZSE$xpos"; 
    

    /**
     * Encrypt the raw string to token
     * @param rawStr
     * @return
     */
    public  String encrypt(String rawStr) {
        if(StringUtils.isNotBlank(rawStr)){
             try {
                 Key aesKey = new SecretKeySpec(DEFAULT_SALT.getBytes(StandardCharsets.UTF_8), "AES");
                 Cipher cipher = Cipher.getInstance("AES");
                 cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                 byte[] encrypted = cipher.doFinal(rawStr.getBytes(StandardCharsets.UTF_8));
                 return new String(EnCryptors.BASE64.encode(encrypted), StandardCharsets.UTF_8);
              
              }catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                  throw new RuntimeException("Error happened when encrypting String = " + rawStr + " due to " + e.getMessage());
              }
        }else{
            throw new RuntimeException("Original String to be encrypted is null");
        }
         
    }
    
    
    /**
     * Decrypt the token to raw string
     * @param encryptedStr
     * @return
     */
    public  String decrypt(String encryptedStr){
        if(StringUtils.isNotBlank(encryptedStr)){
            try {
                Key aesKey = new SecretKeySpec(DEFAULT_SALT.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] baseDecrypted = EnCryptors.BASE64.decode(encryptedStr);
                byte[] decrypted = cipher.doFinal(baseDecrypted);
                return new String(decrypted, StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                 throw new RuntimeException("Error happened when decrypting String = " + encryptedStr + " due to " + e.getMessage());
            }
        }else{
            throw new RuntimeException("Original String to be decrypted is null");
        }
        
        
    }
    
    //refactor ,allow to input encript key values;
    public  String encrypt(String rawStr,String key) {
        if(StringUtils.isNotBlank(rawStr)){
             try {
                 Key aesKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
                 Cipher cipher = Cipher.getInstance("AES");
                 cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                 byte[] encrypted = cipher.doFinal(rawStr.getBytes(StandardCharsets.UTF_8));
                 return new String(EnCryptors.BASE64.encode(encrypted), StandardCharsets.UTF_8);
              
              }catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                  throw new RuntimeException("Error happened when encrypting String = " + rawStr + " due to " + e.getMessage());
              }
        }else{
            throw new RuntimeException("Original String to be encrypted is null");
        }
         
    }
    
    public  String decrypt(String encryptedStr,String key){
        if(StringUtils.isNotBlank(encryptedStr)){
            try {
                Key aesKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] baseDecrypted = EnCryptors.BASE64.decode(encryptedStr);
                byte[] decrypted = cipher.doFinal(baseDecrypted);
                return new String(decrypted, StandardCharsets.UTF_8);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                 throw new RuntimeException("Error happened when decrypting String = " + encryptedStr + " due to " + e.getMessage());
            }
        }else{
            throw new RuntimeException("Original String to be decrypted is null");
        }
        
        
    }
    public static AESCryptor getInstance(){
		return instance;
    }
}
