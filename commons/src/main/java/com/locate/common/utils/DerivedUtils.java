package com.locate.common.utils;

public class DerivedUtils {
	public static final String PRICE_TECH_HEAD = "PT_";
	public static final String RMB_END = "_CYN";

	public static String derivesCurrencyRic(String ric) {
		ric = PRICE_TECH_HEAD + ric + RMB_END;
		return ric;
	}
	
	public static String restoreRic(String derivedRic){
		String originRic = derivedRic.split("_")[1];
		return originRic;
	}
	
	public static boolean isDerived(String itemName){
		return itemName.startsWith(PRICE_TECH_HEAD);
	}
}
