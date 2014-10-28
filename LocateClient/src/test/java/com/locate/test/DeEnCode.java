package com.locate.test;

import java.nio.charset.Charset;

public class DeEnCode {
	private static final String key0 = "FECOI()*&<MNCXZPKL";
//	private static final String key0 = "1";
	private static final Charset charset = Charset.forName("UTF-8");
	private static byte[] keyBytes = key0.getBytes(charset);

	public static byte[] encode(byte[] enc) {
		for (int i = 0, size = enc.length; i < size; i++) {
			for (byte keyBytes0 : keyBytes) {
				enc[i] = (byte) (enc[i] ^ keyBytes0);
			}
		}
		return enc;
	}

	public static String decode(byte[] dec) {
		byte[] dee = dec;
		for (int i = 0, size = dec.length; i < size; i++) {
			for (byte keyBytes0 : keyBytes) {
				dec[i] = (byte) (dec[i] ^ keyBytes0);
			}
		}
		return new String(dee,charset);
	}

	public static void main(String[] args) {
		String s = "you are right`1234567890-=[]';/.,`1234567890-[]';/.,你好吗?我很好呀.";
//		String s = "你好啊短发的父亲为热爱的撒地方";
		byte[] ss=s.getBytes(charset);
		byte[] enc = encode(ss);
		for(int i=0;i<enc.length;i++){
			System.out.println("enc is "+enc[i]);
		}
		String dec = decode(enc);
		System.out.println("dec is "+dec);
	}
}
