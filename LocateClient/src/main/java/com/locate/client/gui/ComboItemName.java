package com.locate.client.gui;

public class ComboItemName {
	private String itemValue;
	public ComboItemName(String itemValue){
		this.itemValue = itemValue;
	}
	
	public static String exhangeTheName(String locateMessageName){
		switch (locateMessageName) {
		case "PT_MCU3=LX_CNY":
			return "普兰泰科自定义铜";
		case "XAU=":
			return "金";
		case "XAG=":
			return "银";
		case "MCU3=LX":
			return "三月铜";
		case "MAL3=LX":
			return "三月铝";
		case "MAA3=LX":
			return "三月合金铝";
		case "MZN3=LX":
			return "三月锌";
		case "MSN3=LX":
			return "三月锡";
		case "MNI3=LX":
			return "三月镍";
		case "MPB3=LX":
			return "三月钯";
		case "MNA3=LX":
			return "北美特种铝";
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
