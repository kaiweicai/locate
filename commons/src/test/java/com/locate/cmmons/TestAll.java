package com.locate.cmmons;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;

import com.locate.common.utils.CryptSecurity;
import com.locate.common.utils.RFAUtils;

public class TestAll {
	@Test
	public void testOutputFunction() throws Exception{
		String s = RFAUtils.output("4c6d2db7b781a2e0");
		System.out.println(s);
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
		String result = CryptSecurity.encryptToDES(key,info);
		System.out.println(result);
		String decrypt = CryptSecurity.decryptByDES(key,result);
		System.out.println(decrypt);
	}
}
