package com.locate.rmds.util;

public class RFATypeConvert {
	
	public static String convertField(String fieldName){
		String newFieldName = "Unknown";
		if(fieldName.contains("INT")){
			newFieldName = "Integer"; 
		}else if(fieldName.contains("STRING")){
			newFieldName = "String";
		}else if(fieldName.contains("REAL")){
			newFieldName = "Double";
		}else if(fieldName.contains("ENUM")){
			newFieldName = "String";
		}
		
		return newFieldName;
	}

	
	public static byte[] intToByteArray1(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;// 
		}
		return value;
	}
	
	public static int byteArrayToInt(byte[] b) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < 4; i++) {
                bLoop = b[i];
                iOutcome += (bLoop & 0xff) << (8 * i);
        }
        return iOutcome;
	}
	
	public static void main(String[] arg){
		int a = 222;
		byte[] b = RFATypeConvert.intToByteArray1(a);
		
		int c = RFATypeConvert.byteArrayToInt(b);
		int d = RFATypeConvert.byteArrayToInt(b, 0);
		System.out.println(c);
		System.out.println(d);
	}
}
