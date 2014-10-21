package com.locate.client.gui;

import com.locate.common.model.InstrumentCodeData;
import com.locate.common.model.InstrumentCodeModel;

public class ComboItemName {
	private String itemValue;
	public ComboItemName(String itemValue){
		this.itemValue = itemValue;
	}
	
	public static String exhangeCode2Name(String itemName){
		String chineseName = null;
		InstrumentCodeModel instrumentCodeMode = InstrumentCodeData.instrumentCodeMap.get(itemName);
		if(instrumentCodeMode!=null){
			chineseName = instrumentCodeMode.getChineseName();
		}else{
			chineseName = itemName;
		}
		return chineseName;
	}
	
	public static String exhangeName2Code(String chineseName){
		String instrumentCode = null;
		InstrumentCodeModel instrumentCodeMode = InstrumentCodeData.chineseNameMap.get(chineseName);
		if(instrumentCodeMode!=null){
			instrumentCode = instrumentCodeMode.getInstrumentCode();
		}else{
			instrumentCode = "未命名商品";
		}
		return instrumentCode;
	}
	
	public String toString(){
		return exhangeCode2Name(itemValue);
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
}
