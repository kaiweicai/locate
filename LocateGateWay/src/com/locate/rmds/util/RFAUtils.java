package com.locate.rmds.util;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.sun.crypto.provider.SunJCE;

public class RFAUtils {

	private static final String Algorithm = "DESede"; // difine
	static final String keyCode = "calter_proxyDecodeEncode";

	public static byte[] eMode(byte[] src) {
		try {
			//
			SecretKey deskey = new SecretKeySpec(keyCode.getBytes(), Algorithm);

			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}



	public static byte[] dMode(byte[] src) throws Exception{


		SecretKey deskey = new SecretKeySpec(keyCode.getBytes(), Algorithm);

		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.DECRYPT_MODE, deskey);
		return c1.doFinal(src);
	}

	public static String input(String str){
		return bth(eMode(str.getBytes()));
	}
	
	public static String output(String str) throws Exception{
		return new String(dMode(htb(str)));
	}
	
	public static String bth(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}  
	public static byte[] htb(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	} 
	public static void main(String[] args) {

		//
		Security.addProvider(new SunJCE());
		// final byte[] keyBytes = {0x11, 0x22, 0x4F, 0x58,
		// (byte)0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79, 0x51,
		// (byte)0xCB, (byte)0xDD, 0x55, 0x66, 0x77, 0x29, 0x74,
		// (byte)0x98, 0x30, 0x40, 0x36, (byte)0xE2
		// };

		String szSrc = "jw2013";

//		byte[] encoded = encryptMode(szSrc.getBytes());
		String encoded = input(szSrc);
		System.out.println(encoded);

		try {
			System.out.println(output(encoded));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testOutputFunction() throws Exception{
		String s =output("4c6d2db7b781a2e0");
		System.out.println(s);
	}
}
