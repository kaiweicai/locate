package com.locate.common;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class CryptSecurity {
	private static Logger logger = Logger.getLogger(CryptSecurity.class);
	public static SecretKey key = null;
	static{
		try {
			key=KeyGenerator.getInstance("DES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据密匙进行DES加密
	 * 
	 * @param key
	 *            密匙
	 * @param info
	 *            要加密的信息
	 * @return String 加密后的信息
	 */
	public static String encryptToDES(SecretKey key,String info) {
		// 定义 加密算法,可用 DES,DESede,Blowfish
		String Algorithm = "DES";
		// 加密随机数生成器 (RNG),(可以不写)
//		SecureRandom sr = new SecureRandom();
		// 定义要生成的密文
		byte[] cipherByte = null;
		try {
			// 得到加密/解密器
			Cipher c1 = Cipher.getInstance(Algorithm);
			// 用指定的密钥和模式初始化Cipher对象
			// 参数:(ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE,UNWRAP_MODE)
//			c1.init(Cipher.ENCRYPT_MODE, key, sr);
			c1.init(Cipher.ENCRYPT_MODE, key);
			// 对要加密的内容进行编码处理,
			cipherByte = c1.doFinal(info.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error("Decrypt String error! String is "+info +e);
		}
		// 返回密文的十六进制形式
		return Base64.encode(cipherByte);
	}

	/**
	 * 根据密匙进行DES解密
	 * 
	 * @param key
	 *            密匙
	 * @param sInfo
	 *            要解密的密文
	 * @return String 返回解密后信息
	 */
	public static String decryptByDES(SecretKey key,String sInfo) {
		// 定义 加密算法,
		String Algorithm = "DES";
		// 加密随机数生成器 (RNG)
//		SecureRandom sr = new SecureRandom();
		byte[] cipherByte = null;
		String result = null;
		try {
			// 得到加密/解密器
			Cipher c1 = Cipher.getInstance(Algorithm);
			// 用指定的密钥和模式初始化Cipher对象
//			c1.init(Cipher.DECRYPT_MODE, key, sr);
			c1.init(Cipher.DECRYPT_MODE, key);
			// 对要解密的内容进行编码处理
			cipherByte = c1.doFinal(Base64.decode(sInfo));
			result = new String(cipherByte, "UTF-8");
		} catch (Exception e) {
			logger.error("Decrypt error! String is "+sInfo+e);
		}
		// return byte2hex(cipherByte);
		return result;
	}

	/**
	 * 将二进制转化为16进制字符串
	 * 
	 * @param b
	 *            二进制字节数组
	 * @return String
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * 十六进制字符串转化为2进制
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hex2byte(String hex) {
		byte[] ret = new byte[8];
		byte[] tmp = hex.getBytes();
		for (int i = 0; i < 8; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	@Test
	public void test() {
		String info = "魏勇阿德克里夫奥丢分快乐abccdefadfadskllkjalkdfdklfajdsl卡机的法律框架fkjaklsf;lgs l;retpofsl;gskdfl;gks;fgklvb;sldfkg'lwekrptoks;lfg;lsdfgkls;dfkg;lsdfkglkjlks";
		SecretKey key = null;
		try {
			key = KeyGenerator.getInstance("DES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println(key.getEncoded());
		String result = encryptToDES(key,info);
		System.out.println(result);
		String decrypt = decryptByDES(key,result);
		System.out.println(decrypt);
	}
}
