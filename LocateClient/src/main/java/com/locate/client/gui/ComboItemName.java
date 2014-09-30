package com.locate.client.gui;

public class ComboItemName {
	private String itemValue;
	public ComboItemName(String itemValue){
		this.itemValue = itemValue;
	}
	
	public static String exhangeTheName(String locateMessageName){
		switch (locateMessageName) {
		case "XAU=":
			return "国际黄金";
		case "XAG=":
			return "国际白银";
		case "XPT=":
			return "国际铂金";
		case "XPD=":
			return "国际钯金";
		case "MCU3=LX":
			return "伦敦三月期铜";
		case "MAL3=LX":
			return "伦敦三月期铝";
		case "MPB3=LX":
			return "伦敦三月期铅";
		case "MZN3=LX":
			return "伦敦三月期锌";
		case "MNI3=LX":
			return "伦敦三月期镍";	
		case "GCv1":
			return "COMEX黄金期货";
		case "SIv1":
			return "COMEX白银期货";
		case "PLv1":
			return "NYMEX铂金";
		case "PAv1":
			return "NYMEX钯金";
		case "CLv1":
			return "WTI原油主力合约";
		case "LCOv1":
			return "BRENT原油主力合约";			
		default:
			return locateMessageName;
		}
	}
	
	public String toString(){
		return exhangeTheName(itemValue);
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
}
