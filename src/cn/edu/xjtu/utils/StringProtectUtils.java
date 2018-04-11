package cn.edu.xjtu.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


/**
 * 
 * @author zhangchen
 *
 */
public class StringProtectUtils {
	public static final String KEY_MAC="HmacMD5";
	public static String getMd5(String str){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte[] result=md5.digest();
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	
	public static String encryptHMAC(String data, String keyHMAC) {
		Mac mac =null; 
		byte[] secretByte;
		byte[] dataBytes = null;
		try {
			mac=Mac.getInstance("HmacSHA256");
			secretByte= keyHMAC.getBytes("UTF-8");
			dataBytes= data.getBytes("UTF-8");
			SecretKey secret = new SecretKeySpec(secretByte, "HMACSHA256");

			mac.init(secret);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] doFinal = mac.doFinal(dataBytes);
		byte[] hexB = new Hex().encode(doFinal);
		String checksum = new String(hexB);
		return checksum.substring(0,16);
	}  
    
}
