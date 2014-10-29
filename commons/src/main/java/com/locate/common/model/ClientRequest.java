package com.locate.common.model;


public class ClientRequest {
	private String userName;
	private String password;
	private String ItemCode;
	private int channelID;
	private byte msgType;
	private String clientIP;
	private String engineId;
	public ClientRequest(){
	}
	
	public String getItemCode() {
		return ItemCode;
	}

	public void setItemCode(String itemCode) {
		ItemCode = itemCode;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getChannelID() {
		return channelID;
	}
	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}
	public byte getMsgType() {
		return msgType;
	}
	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getEngineId() {
		return engineId;
	}

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}

	@Override
	public String toString() {
		return "ClientRequest [userName=" + userName + ", password=" + password + ", RIC=" + ItemCode + ", channelID="
				+ channelID + ", msgType=" + msgType + ", clientIP=" + clientIP + ", engineId=" + engineId + "]";
	}

	public void encapNetInfo(String userName, int channelId, String clientIP) {
		this.userName = userName;
		this.channelID = channelId;
		this.clientIP = clientIP;
	}
	
}
