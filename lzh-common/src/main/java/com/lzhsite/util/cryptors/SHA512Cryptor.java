package com.lzhsite.util.cryptors;

import org.apache.shiro.crypto.hash.Sha512Hash;


public class SHA512Cryptor {
	private SHA512Cryptor(){}
	private static SHA512Cryptor instance=new SHA512Cryptor();
    
    public  String encrypt(String rawPassword, String salt) {
		return new Sha512Hash(rawPassword, salt, 512).toBase64();
	}
    
    public static  SHA512Cryptor getInstance(){
		return instance;
    }
}
