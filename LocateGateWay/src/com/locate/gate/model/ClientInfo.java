package com.locate.gate.model;

import org.dom4j.Document;

public class ClientInfo {
	private Document userRquest;
	private String userName;
	private int channelID;
	private byte msgType;
	private String clientIP;
	
	public ClientInfo(Document userRquest, String userName, int channelID, byte msgType, String clientIP) {
		super();
		this.userRquest = userRquest;
		this.userName = userName;
		this.channelID = channelID;
		this.msgType = msgType;
		this.clientIP = clientIP;
	}
	
	public Document getUserRquest() {
		return userRquest;
	}
	public void setUserRquest(Document userRquest) {
		this.userRquest = userRquest;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
		return "ClientInfo [userRquest=" + userRquest + ", userName=" + userName + ", channelID=" + channelID
				+ ", msgType=" + msgType + ", clientIP=" + clientIP + "]";
	}
}
