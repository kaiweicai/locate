package com.locate.common.model;

import org.dom4j.Document;

import com.locate.common.SystemConstant;

public class ClientRequest {
	private long locateSequenceNo;
	private String RIC;
	private String userName;
	private String password;
	private int channelID;
	private byte msgType;
	private String clientIP;
	
	public ClientRequest(){
		this.locateSequenceNo = SystemConstant.sequenceNo.incrementAndGet();
	}
	
	public ClientRequest(Document userRquest, String userName, int channelID, byte msgType, String clientIP) {
		this.locateSequenceNo = SystemConstant.sequenceNo.incrementAndGet();
		this.userName = userName;
		this.channelID = channelID;
		this.msgType = msgType;
		this.clientIP = clientIP;
	}
	
	public long getLocateSequenceNo() {
		return locateSequenceNo;
	}

	public void setLocateSequenceNo(long locateSequenceNo) {
		this.locateSequenceNo = locateSequenceNo;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getRIC() {
		return RIC;
	}

	public void setRIC(String rIC) {
		RIC = rIC;
	}
	
	@Override
	public String toString() {
		return "ClientInfo [ userName=" + userName + ", channelID=" + channelID
				+ ", msgType=" + msgType + ", clientIP=" + clientIP + "]";
	}
}
