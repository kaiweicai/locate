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
	 * �����ܳ׽���DES����
	 * 
	 * @param key
	 *            �ܳ�
	 * @param info
	 *            Ҫ���ܵ���Ϣ
	 * @return String ���ܺ����Ϣ
	 */
	public static String encryptToDES(SecretKey key,String info) {
		// ���� �����㷨,���� DES,DESede,Blowfish
		String Algorithm = "DES";
		// ��������������� (RNG),(���Բ�д)
//		SecureRandom sr = new SecureRandom();
		// ����Ҫ���ɵ�����
		byte[] cipherByte = null;
		try {
			// �õ�����/������
			Cipher c1 = Cipher.getInstance(Algorithm);
			// ��ָ������Կ��ģʽ��ʼ��Cipher����
			// ����:(ENCRYPT_MODE, DECRYPT_MODE, WRAP_MODE,UNWRAP_MODE)
//			c1.init(Cipher.ENCRYPT_MODE, key, sr);
			c1.init(Cipher.ENCRYPT_MODE, key);
			// ��Ҫ���ܵ����ݽ��б��봦��,
			cipherByte = c1.doFinal(info.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error("Decrypt String error! String is "+info +e);
		}
		// �������ĵ�ʮ��������ʽ
		return Base64.encode(cipherByte);
	}

	/**
	 * �����ܳ׽���DES����
	 * 
	 * @param key
	 *            �ܳ�
	 * @param sInfo
	 *            Ҫ���ܵ�����
	 * @return String ���ؽ��ܺ���Ϣ
	 */
	public static String decryptByDES(SecretKey key,String sInfo) {
		// ���� �����㷨,
		String Algorithm = "DES";
		// ��������������� (RNG)
//		SecureRandom sr = new SecureRandom();
		byte[] cipherByte = null;
		String result = null;
		try {
			// �õ�����/������
			Cipher c1 = Cipher.getInstance(Algorithm);
			// ��ָ������Կ��ģʽ��ʼ��Cipher����
//			c1.init(Cipher.DECRYPT_MODE, key, sr);
			c1.init(Cipher.DECRYPT_MODE, key);
			// ��Ҫ���ܵ����ݽ��б��봦��
			cipherByte = c1.doFinal(Base64.decode(sInfo));
			result = new String(cipherByte, "UTF-8");
		} catch (Exception e) {
			logger.error("Decrypt error! String is "+sInfo+e);
		}
		// return byte2hex(cipherByte);
		return result;
	}

	/**
	 * ��������ת��Ϊ16�����ַ���
	 * 
	 * @param b
	 *            �������ֽ�����
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
	 * ʮ�������ַ���ת��Ϊ2����
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
	 * ������ASCII�ַ��ϳ�һ���ֽڣ� �磺"EF"--> 0xEF
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
		String info = "κ�°��¿����¶��ֿ���abccdefadfadskllkjalkdfdklfajdsl�����ķ��ɿ��fkjaklsf;lgs l;retpofsl;gskdfl;gks;fgklvb;sldfkg'lwekrptoks;lfg;lsdfgkls;dfkg;lsdfkglkjlks";
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
