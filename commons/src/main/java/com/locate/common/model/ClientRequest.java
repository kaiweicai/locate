package com.locate.common.model;


public class ClientRequest {
	private ClientRequest clientRequest;
	private String userName;
	private String password;
	private String RIC;
	private int channelID;
	private byte msgType;
	private String clientIP;
	
	public ClientRequest(){
		
	}
	
	public ClientRequest(ClientRequest userRquest, String userName, int channelID,  String clientIP) {
		super();
		this.clientRequest = userRquest;
		this.userName = userName;
		this.channelID = channelID;
		this.msgType = userRquest.getMsgType();
		this.clientIP = clientIP;
	}
	
	public String getRIC() {
		return RIC;
	}

	public void setRIC(String rIC) {
		RIC = rIC;
	}

	public ClientRequest getClientRequest() {
		return clientRequest;
	}

	public void setClientRequest(ClientRequest userRquest) {
		this.clientRequest = userRquest;
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

	@Override
	public String toString() {
		return "ClientInfo [userRquest=" + clientRequest + ", userName=" + userName + ", channelID=" + channelID
				+ ", msgType=" + msgType + ", clientIP=" + clientIP + "]";
	}
}
