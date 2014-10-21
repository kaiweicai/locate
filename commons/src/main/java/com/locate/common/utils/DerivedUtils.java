package com.locate.common.utils;

public class DerivedUtils {
	public static final String DERIVED_HEAD = "DE_";
	public static final String RMB_END = "_CNY";

	public static String derivesCurrencyRic(String ric) {
		ric = DERIVED_HEAD + ric + RMB_END;
		return ric;
	}
	
	public static String restoreRic(String derivedRic){
		String originRic = derivedRic.split("_")[1];
		return originRic;
	}
	
	public static boolean isDerived(String itemName){
		return itemName.startsWith(DERIVED_HEAD);
	}
}
