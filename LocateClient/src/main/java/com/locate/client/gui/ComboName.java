package com.locate.client.gui;

public class ComboName {
	public ComboName(String value) {
		this.key = getServerName(value);
		this.value = value;
	}
	
	public String getServerName(String serverIP){
		switch (serverIP) {
		case "127.0.0.1":
			return "本地服务器";
		case "61.144.244.149":
			return "深圳服务器1";
		case "61.144.244.173":
			return "上海服务器1";
		default:
			return serverIP;
		}
	}

	private String value;

	private String key;


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return getKey();
	}
}
